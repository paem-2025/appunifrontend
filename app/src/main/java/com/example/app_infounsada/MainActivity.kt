package com.example.app_infounsada

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.app_infounsada.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

        val detectedTopic = TopicCatalog.detectTopic(question)
        if (detectedTopic == null) {
            binding.tvAssistantAnswer.text =
                "Puedo llevarte a: Calendario, Becas, Plataformas, Tutorias, Ingresantes, Tramites y Carreras."
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
}
