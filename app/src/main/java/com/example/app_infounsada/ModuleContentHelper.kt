package com.example.app_infounsada

object ModuleContentHelper {
    fun mapFinalExamToModule(exam: FinalExamResponse): ModuleResponse {
        val turn = exam.turnOrder?.let { "Turno $it" } ?: "Turno"
        val title = listOf(turn, exam.turnName, exam.periodLabel)
            .filter { !it.isNullOrBlank() }
            .joinToString(" - ")
            .ifBlank { "Mesa final ${exam.examYear ?: ""}".trim() }

        return ModuleResponse(
            idmodule = exam.idfinalExam,
            title = title,
            content = buildFinalExamContent(exam),
            topicName = "Calendario Academico",
            sourceUrl = exam.sourceUrl
        )
    }

    private fun buildFinalExamContent(exam: FinalExamResponse): String {
        val lines = mutableListOf<String>()
        exam.examYear?.let { lines.add("Ano: $it") }
        exam.enrollmentStart?.let { lines.add("Inscripcion desde: ${formatIsoDate(it)}") }
        val examStart = formatIsoDate(exam.examStart)
        val examEnd = formatIsoDate(exam.examEnd)
        if (examStart.isNotBlank() || examEnd.isNotBlank()) {
            lines.add("Mesas: $examStart al $examEnd")
        }
        if (!exam.notes.isNullOrBlank()) lines.add(exam.notes)
        return lines.joinToString("\n")
    }

    fun mapCorrelativityToModule(item: CorrelativityResponse): ModuleResponse {
        val subjectCode = item.subjectCode?.let { "[$it] " } ?: ""
        val title = "$subjectCode${item.subjectName.orEmpty()}".ifBlank { "Correlatividad" }

        val contentLines = mutableListOf<String>()
        item.careerName?.takeIf { it.isNotBlank() }?.let { contentLines.add("Carrera: $it") }
        val planLabel = buildList {
            item.planName?.takeIf { it.isNotBlank() }?.let { add(it) }
            item.planYear?.let { add(it.toString()) }
        }.joinToString(" - ")
        if (planLabel.isNotBlank()) contentLines.add("Plan: $planLabel")
        item.subjectYear?.let { contentLines.add("Ano sugerido: $it") }
        item.subjectTerm?.takeIf { it.isNotBlank() }?.let { contentLines.add("Cuatrimestre: $it") }

        if (!item.requirementSubjects.isNullOrBlank()) {
            contentLines.add("Correlativas: ${item.requirementSubjects}")
        } else {
            contentLines.add("Correlativas: sin correlativas previas")
        }

        item.notes?.takeIf { it.isNotBlank() }?.let { contentLines.add(it) }

        return ModuleResponse(
            idmodule = item.idcorrelativity,
            title = title,
            content = contentLines.joinToString("\n"),
            topicName = "Correlatividades",
            sourceUrl = item.sourceUrl
        )
    }

    fun mapAlertToModule(item: AppAlertResponse): ModuleResponse {
        val tag = if (item.important == true) "Importante" else "Info"
        val title = "[$tag] ${item.title.orEmpty()}".ifBlank { "Alerta" }

        val contentLines = mutableListOf<String>()
        item.category?.takeIf { it.isNotBlank() }?.let { contentLines.add("Categoria: $it") }
        item.audience?.takeIf { it.isNotBlank() }?.let { contentLines.add("Para: $it") }
        item.alertDate?.let { contentLines.add("Desde: ${formatIsoDate(it)}") }
        item.endDate?.takeIf { it.isNotBlank() }?.let { contentLines.add("Hasta: ${formatIsoDate(it)}") }
        item.message?.takeIf { it.isNotBlank() }?.let { contentLines.add(it) }

        return ModuleResponse(
            idmodule = item.idappAlert,
            title = title,
            content = contentLines.joinToString("\n"),
            topicName = "Alertas",
            sourceUrl = item.sourceUrl
        )
    }

    fun mapFaqToModule(item: IngresanteFaqResponse): ModuleResponse {
        val title = item.question?.ifBlank { "Pregunta frecuente" } ?: "Pregunta frecuente"
        val contentLines = mutableListOf<String>()
        item.category?.takeIf { it.isNotBlank() }?.let { contentLines.add("Tema: $it") }
        item.answer?.takeIf { it.isNotBlank() }?.let { contentLines.add(it) }

        return ModuleResponse(
            idmodule = item.idfaq,
            title = title,
            content = contentLines.joinToString("\n"),
            topicName = "FAQ Ingresantes",
            sourceUrl = item.sourceUrl
        )
    }

    fun formatIsoDate(value: String?): String {
        if (value.isNullOrBlank()) return ""
        if (value.length == 10 && value[4] == '-' && value[7] == '-') {
            val year = value.substring(0, 4)
            val month = value.substring(5, 7)
            val day = value.substring(8, 10)
            return "$day/$month/$year"
        }
        return value
    }

    fun extractFirstUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val directMatch = Regex("""https?://\S+""", RegexOption.IGNORE_CASE).find(text)
        if (directMatch != null) {
            return normalizeUrl(directMatch.value)
        }

        val domainMatch = Regex(
            """\b(?:www\.)?[a-z0-9-]+(?:\.[a-z0-9-]+)+(?:/[^\s]*)?""",
            RegexOption.IGNORE_CASE
        ).find(text)

        return normalizeUrl(domainMatch?.value)
    }

    fun normalizeUrl(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val cleaned = raw.trim().trimEnd('.', ',', ';', ')', ']', '}', '"', '\'')
        if (cleaned.isBlank()) return null
        return if (cleaned.startsWith("http://", true) || cleaned.startsWith("https://", true)) {
            cleaned
        } else {
            "https://$cleaned"
        }
    }
}
