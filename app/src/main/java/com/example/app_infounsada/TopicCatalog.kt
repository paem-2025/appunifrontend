package com.example.app_infounsada

import java.text.Normalizer
import java.util.Locale

object TopicCatalog {
    val topics = listOf(
        "Calendario Academico",
        "Becas",
        "Plataformas",
        "Tutorias",
        "Ingresantes",
        "Tramites",
        "Carreras",
        "Correlatividades",
        "Alertas",
        "FAQ Ingresantes"
    )

    fun detectTopic(question: String): String? {
        val q = normalize(question)
        return when {
            q.contains("mesa") || q.contains("final") || q.contains("examen") || q.contains("calendario") ->
                "Calendario Academico"
            q.contains("beca") || q.contains("progresar") || q.contains("pubu") ->
                "Becas"
            q.contains("plataforma") || q.contains("guarani") || q.contains("moodle") ||
                q.contains("correo") || q.contains("nexos") ->
                "Plataformas"
            q.contains("tutoria") || q.contains("acompanamiento") ->
                "Tutorias"
            q.contains("ingresante") || q.contains("nuevo") || q.contains("primer") ||
                q.contains("inicio") || q.contains("empezar") ->
                "Ingresantes"
            q.contains("tramite") || q.contains("equivalencia") ||
                q.contains("readmision") || q.contains("simultaneidad") ||
                q.contains("cambio de carrera") ->
                "Tramites"
            q.contains("carrera") || q.contains("carreras") ||
                q.contains("oferta academica") ||
                q.contains("licenciatura") || q.contains("tecnicatura") ||
                q.contains("ingenieria") ||
                q.contains("enfermeria") ||
                q.contains("informatica") ||
                q.contains("fonoaudiologia") ->
                "Carreras"
            q.contains("correlativa") || q.contains("correlatividad") || q.contains("materia previa") ->
                "Correlatividades"
            q.contains("alerta") || q.contains("recordatorio") || q.contains("vencimiento") || q.contains("fecha limite") ->
                "Alertas"
            q.contains("faq") || q.contains("pregunta frecuente") || q.contains("no entiendo") || q.contains("verg") ->
                "FAQ Ingresantes"
            else -> null
        }
    }

    private fun normalize(input: String): String {
        val noAccents = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
        return noAccents.lowercase(Locale.ROOT)
    }
}
