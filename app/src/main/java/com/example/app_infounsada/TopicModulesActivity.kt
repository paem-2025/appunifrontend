package com.example.app_infounsada

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_infounsada.databinding.ActivityTopicModulesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TopicModulesActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityTopicModulesBinding
    private val apiService: APIService by lazy { ApiFactory.createService() }
    private val moduleAdapter = ModuleAdapter { module ->
        openModuleSource(module)
    }
    private var selectedTopic = "Calendario Academico"
    private var loadedModules: List<ModuleResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicModulesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedTopic = intent.getStringExtra(EXTRA_TOPIC_NAME) ?: selectedTopic

        setupHeader()
        setupRecyclerView()
        setupSearch()
        loadContentByTopic(selectedTopic)
    }

    private fun setupHeader() {
        binding.tvTopicTitle.text = selectedTopic
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.rvModules.layoutManager = LinearLayoutManager(this)
        binding.rvModules.adapter = moduleAdapter
    }

    private fun setupSearch() {
        binding.svModules.setOnQueryTextListener(this)
    }

    private fun loadContentByTopic(topicName: String) {
        lifecycleScope.launch {
            val modules = fetchTopicContent(topicName)
            if (modules == null) {
                Toast.makeText(this@TopicModulesActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                return@launch
            }
            loadedModules = modules
            moduleAdapter.submitList(loadedModules)
            updateResultsTitle(loadedModules.size)
        }
    }

    private suspend fun fetchTopicContent(topicName: String): List<ModuleResponse>? {
        return try {
            when {
                topicName.equals("Calendario Academico", ignoreCase = true) -> {
                    val call = withContext(Dispatchers.IO) { apiService.getCurrentYearFinalExams() }
                    if (!call.isSuccessful) return null
                    call.body().orEmpty().map { exam -> ModuleContentHelper.mapFinalExamToModule(exam) }
                }

                topicName.equals("Correlatividades", ignoreCase = true) -> {
                    val call = withContext(Dispatchers.IO) { apiService.getAllCorrelativities() }
                    if (!call.isSuccessful) return null
                    call.body().orEmpty().map { row -> ModuleContentHelper.mapCorrelativityToModule(row) }
                }

                topicName.equals("Alertas", ignoreCase = true) -> {
                    val call = withContext(Dispatchers.IO) { apiService.getActiveAlerts() }
                    if (!call.isSuccessful) return null
                    call.body().orEmpty().map { row -> ModuleContentHelper.mapAlertToModule(row) }
                }

                topicName.equals("FAQ Ingresantes", ignoreCase = true) -> {
                    val call = withContext(Dispatchers.IO) { apiService.getIngresanteFaq() }
                    if (!call.isSuccessful) return null
                    call.body().orEmpty().map { row -> ModuleContentHelper.mapFaqToModule(row) }
                }

                else -> {
                    val call = withContext(Dispatchers.IO) { apiService.getModulesByTopicName(topicName) }
                    if (!call.isSuccessful) return null
                    call.body().orEmpty()
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun searchModules(query: String) {
        val filtered = if (query.isBlank()) {
            loadedModules
        } else {
            loadedModules.filter {
                (it.title?.contains(query, ignoreCase = true) == true) ||
                    (it.content?.contains(query, ignoreCase = true) == true)
            }
        }
        moduleAdapter.submitList(filtered)
        updateResultsTitle(filtered.size)
    }

    private fun updateResultsTitle(count: Int) {
        binding.tvResultsTitle.text = "Contenido de $selectedTopic ($count)"
    }

    private fun openModuleSource(module: ModuleResponse) {
        val url = ModuleContentHelper.normalizeUrl(module.sourceUrl)
            ?: ModuleContentHelper.extractFirstUrl(module.content)
        if (url.isNullOrBlank()) {
            Toast.makeText(this, "Este modulo no tiene enlace fuente", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(this, "No pude abrir el enlace de fuente", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchModules(query.orEmpty())
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchModules(newText.orEmpty())
        return true
    }

    companion object {
        const val EXTRA_TOPIC_NAME = "extra_topic_name"
    }
}
