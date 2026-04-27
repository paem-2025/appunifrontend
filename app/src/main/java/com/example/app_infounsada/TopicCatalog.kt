package com.example.app_infounsada

object TopicCatalog {
    val topics = listOf(
        "Calendario Academico",
        "Becas",
        "Plataformas",
        "Tutorias",
        "Ingresantes",
        "Tramites",
        "Carreras"
    )

    fun detectTopic(question: String): String? {
        val q = question.lowercase()
        return when {
            q.contains("mesa") || q.contains("final") || q.contains("examen") || q.contains("calendario") ->
                "Calendario Academico"
            q.contains("beca") || q.contains("progresar") || q.contains("pubu") ->
                "Becas"
            q.contains("plataforma") || q.contains("guarani") || q.contains("moodle") ||
                q.contains("correo") || q.contains("nexos") ->
                "Plataformas"
            q.contains("tutoria") || q.contains("tutoria") || q.contains("tutoría") ->
                "Tutorias"
            q.contains("ingresante") || q.contains("nuevo") || q.contains("primer") ||
                q.contains("inicio") || q.contains("empezar") ->
                "Ingresantes"
            q.contains("tramite") || q.contains("trámite") || q.contains("equivalencia") ||
                q.contains("readmision") || q.contains("readmisión") || q.contains("simultaneidad") ||
                q.contains("cambio de carrera") ->
                "Tramites"
            q.contains("carrera") || q.contains("carreras") ||
                q.contains("oferta academica") || q.contains("oferta académica") ||
                q.contains("licenciatura") || q.contains("tecnicatura") ||
                q.contains("ingenieria") || q.contains("ingeniería") ||
                q.contains("enfermeria") || q.contains("enfermería") ||
                q.contains("informatica") || q.contains("informática") ||
                q.contains("fonoaudiologia") || q.contains("fonoaudiología") ->
                "Carreras"
            else -> null
        }
    }
}
