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
