package com.example.app_infounsada

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.app_infounsada.databinding.ActivityMainBinding
import androidx.lifecycle.lifecycleScope
import java.text.Normalizer
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val apiService: APIService by lazy { ApiFactory.createService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTopicMenu()
        setupAssistant()
    }

    private fun setupTopicMenu() {
        binding.btnTopicMenu.setOnClickListener {
            val popupMenu = PopupMenu(this, binding.btnTopicMenu)
            TopicCatalog.topics.forEachIndexed { index, topic ->
                popupMenu.menu.add(0, index, index, topic)
            }
            popupMenu.setOnMenuItemClickListener { item ->
                openTopicScreen(TopicCatalog.topics[item.itemId])
                true
            }
            popupMenu.show()
        }
    }

    private fun setupAssistant() {
        binding.btnAssistantAsk.setOnClickListener {
            askAssistant()
        }
        binding.etAssistantQuestion.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                askAssistant()
                true
            } else {
                false
            }
        }
    }

    private fun askAssistant() {
        val question = binding.etAssistantQuestion.text?.toString()?.trim().orEmpty()
        if (question.isBlank()) {
            binding.tvAssistantAnswer.text = "Escribime algo como: carreras, becas, mesas finales o tramites."
            return
        }

        if (shouldAnswerCorrelativityDirectly(question)) {
            answerCorrelativityQuestion(question)
            return
        }

        val detectedTopic = TopicCatalog.detectTopic(question)
        if (detectedTopic == null) {
            binding.tvAssistantAnswer.text = "Puedo llevarte a: ${TopicCatalog.topics.joinToString(", ")}."
            return
        }

        binding.tvAssistantAnswer.text = "Te abro la seccion '$detectedTopic'."
        openTopicScreen(detectedTopic)
    }

    private fun openTopicScreen(topic: String) {
        val intent = Intent(this, TopicModulesActivity::class.java)
        intent.putExtra(TopicModulesActivity.EXTRA_TOPIC_NAME, topic)
        startActivity(intent)
    }

    private fun answerCorrelativityQuestion(question: String) {
        lifecycleScope.launch {
            binding.tvAssistantAnswer.text = "Buscando correlativas..."

            val call = withContext(Dispatchers.IO) { apiService.getAllCorrelativities() }
            if (!call.isSuccessful) {
                binding.tvAssistantAnswer.text = "No pude consultar correlatividades ahora. Probá de nuevo en un ratito."
                return@launch
            }

            val rows = call.body().orEmpty()
            if (rows.isEmpty()) {
                binding.tvAssistantAnswer.text = "No hay correlatividades cargadas en este momento."
                return@launch
            }

            binding.tvAssistantAnswer.text = buildCorrelativityAnswer(question, rows)
        }
    }

    private fun buildCorrelativityAnswer(
        question: String,
        rows: List<CorrelativityResponse>
    ): String {
        val normalizedQuestion = normalizeForMatch(question)
        val detectedCareer = detectCareerFromQuestion(normalizedQuestion, rows)
        val subjectQuery = extractSubjectQuery(normalizedQuestion, detectedCareer)

        if (subjectQuery.isBlank()) {
            return "Decime la materia puntual. Ejemplo: cual es la correlativa de Ingenieria de Software 1 en Licenciatura en Informatica."
        }

        val searchRows = if (detectedCareer == null) rows else rows.filter {
            normalizeForMatch(it.careerName.orEmpty()) == normalizeForMatch(detectedCareer)
        }

        val bestMatch = searchRows
            .map { it to subjectScore(subjectQuery, normalizeForMatch(it.subjectName.orEmpty())) }
            .filter { it.second > 0.45 }
            .maxByOrNull { it.second }
            ?.first

        if (bestMatch == null) {
            val prefix = if (detectedCareer == null) "" else " en $detectedCareer"
            return "No encontre esa materia$prefix. Probá con el nombre tal cual figura en el plan."
        }

        val requirementNames = resolveRequirementNames(bestMatch, rows)
        val subject = cleanText(bestMatch.subjectName).ifBlank { "esa materia" }
        val career = cleanText(bestMatch.careerName).ifBlank { "esa carrera" }

        return if (requirementNames.isEmpty()) {
            "Para $subject en $career: no tiene correlativas previas."
        } else {
            "Para $subject en $career necesitás: ${requirementNames.joinToString(", ")}."
        }
    }

    private fun resolveRequirementNames(
        row: CorrelativityResponse,
        allRows: List<CorrelativityResponse>
    ): List<String> {
        val byCareer = allRows.filter {
            normalizeForMatch(it.careerName.orEmpty()) == normalizeForMatch(row.careerName.orEmpty())
        }

        val codes = row.requirementCodes
            ?.split(",", ";", "/", " ")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            .orEmpty()

        val fromCodes = codes.mapNotNull { code ->
            byCareer.firstOrNull { it.subjectCode.equals(code, ignoreCase = true) }?.subjectName
        }.map { cleanText(it) }

        if (fromCodes.isNotEmpty()) return fromCodes.distinct()

        return row.requirementSubjects
            ?.split(",", ";")
            ?.map { cleanText(it) }
            ?.filter { it.isNotBlank() }
            .orEmpty()
    }

    private fun detectCareerFromQuestion(
        normalizedQuestion: String,
        rows: List<CorrelativityResponse>
    ): String? {
        val careers = rows.mapNotNull { it.careerName }.distinct()
        return careers.maxByOrNull { career ->
            subjectScore(normalizedQuestion, normalizeForMatch(career))
        }?.takeIf { subjectScore(normalizedQuestion, normalizeForMatch(it)) >= 0.75 }
    }

    private fun extractSubjectQuery(normalizedQuestion: String, detectedCareer: String?): String {
        var query = normalizedQuestion
            .replace("cual es la correlativa de", "")
            .replace("cual es correlativa de", "")
            .replace("que correlativa tiene", "")
            .replace("que necesito para cursar", "")
            .replace("correlativa de", "")
            .replace("correlativa para", "")
            .replace("correlativas de", "")
            .replace("correlativas para", "")
            .replace("materia previa de", "")
            .replace("materia previa para", "")
            .trim()

        if (detectedCareer != null) {
            query = query.replace(normalizeForMatch(detectedCareer), "").trim()
        }
        return query
    }

    private fun isCorrelativityQuestion(question: String): Boolean {
        val q = normalizeForMatch(question)
        return q.contains("correlativa") || q.contains("correlatividad") || q.contains("materia previa")
    }

    private fun shouldAnswerCorrelativityDirectly(question: String): Boolean {
        val q = normalizeForMatch(question)
        if (!isCorrelativityQuestion(question)) return false

        val genericPrompts = setOf(
            "correlativa",
            "correlativas",
            "correlatividad",
            "correlatividades",
            "materia previa",
            "materias previas"
        )
        if (q in genericPrompts) return false

        val hasVerbOrPreposition = q.contains(" de ") || q.contains(" para ") ||
            q.contains(" cual ") || q.contains(" que ") || q.contains(" necesito ")
        return hasVerbOrPreposition
    }

    private fun subjectScore(query: String, target: String): Double {
        if (query.isBlank() || target.isBlank()) return 0.0
        if (target == query) return 1.0
        if (target.contains(query) || query.contains(target)) return 0.95

        val queryTokens = query.split(" ").filter { it.isNotBlank() }.toSet()
        val targetTokens = target.split(" ").filter { it.isNotBlank() }.toSet()
        if (queryTokens.isEmpty() || targetTokens.isEmpty()) return 0.0

        val common = queryTokens.intersect(targetTokens).size.toDouble()
        return common / queryTokens.size.toDouble()
    }

    private fun normalizeForMatch(input: String): String {
        val repaired = repairMojibake(input)
        val noAccents = Normalizer.normalize(repaired, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
        val clean = noAccents.lowercase(Locale.ROOT)
            .replace("[^a-z0-9 ]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()

        val romanToNumber = mapOf(
            "i" to "1", "ii" to "2", "iii" to "3", "iv" to "4", "v" to "5",
            "vi" to "6", "vii" to "7", "viii" to "8", "ix" to "9", "x" to "10"
        )

        return clean.split(" ")
            .filter { it.isNotBlank() }
            .map { romanToNumber[it] ?: it }
            .joinToString(" ")
    }

    private fun repairMojibake(input: String): String {
        return input
            .replace("Ã¡", "á")
            .replace("Ã©", "é")
            .replace("Ã­", "í")
            .replace("Ã³", "ó")
            .replace("Ãº", "ú")
            .replace("Ã±", "ñ")
            .replace("Ã", "Á")
            .replace("Ã‰", "É")
            .replace("Ã", "Í")
            .replace("Ã“", "Ó")
            .replace("Ãš", "Ú")
            .replace("Ã‘", "Ñ")
            .replace("ã¡", "á")
            .replace("ã©", "é")
            .replace("ã­", "í")
            .replace("ã³", "ó")
            .replace("ãº", "ú")
            .replace("ã±", "ñ")
            .replace("Â", "")
    }

    private fun cleanText(input: String?): String {
        return repairMojibake(input.orEmpty()).trim()
    }
}
