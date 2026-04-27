package com.example.app_infounsada

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ModuleAdapter(
    private val onItemClick: (ModuleResponse) -> Unit
) : RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>() {

    private val items = mutableListOf<ModuleResponse>()

    fun submitList(newItems: List<ModuleResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_module, parent, false)
        return ModuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount(): Int = items.size

    class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val topicText: TextView = itemView.findViewById(R.id.tvModuleTopic)
        private val titleText: TextView = itemView.findViewById(R.id.tvModuleTitle)
        private val previewText: TextView = itemView.findViewById(R.id.tvModulePreview)
        private val actionText: TextView = itemView.findViewById(R.id.tvModuleAction)

        fun bind(item: ModuleResponse, onItemClick: (ModuleResponse) -> Unit) {
            topicText.text = item.topicName ?: "General"
            titleText.text = item.title ?: "Sin titulo"
            previewText.text = createPreview(item.content)
            val hasSource = hasUrl(item.content, item.sourceUrl)
            actionText.text = if (hasSource) "Abrir fuente" else "Sin enlace de fuente"
            actionText.alpha = if (hasSource) 1f else 0.6f
            itemView.setOnClickListener { onItemClick(item) }
            actionText.setOnClickListener { onItemClick(item) }
        }

        private fun createPreview(content: String?): String {
            if (content.isNullOrBlank()) return "Sin resumen disponible."
            val clean = content.replace("\\s+".toRegex(), " ").trim()
            return if (clean.length > 160) clean.take(157) + "..." else clean
        }

        private fun hasUrl(content: String?, sourceUrl: String?): Boolean {
            if (!sourceUrl.isNullOrBlank()) return true
            if (content.isNullOrBlank()) return false
            val hasDirectUrl = Regex("""https?://\S+""", RegexOption.IGNORE_CASE).containsMatchIn(content)
            if (hasDirectUrl) return true
            return Regex(
                """\b(?:www\.)?[a-z0-9-]+(?:\.[a-z0-9-]+)+(?:/[^\s]*)?""",
                RegexOption.IGNORE_CASE
            ).containsMatchIn(content)
        }
    }
}
