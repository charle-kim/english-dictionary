package com.example.englishdictionary

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.englishdictionary.databinding.ActivityMainBinding
import com.example.englishdictionary.repository.LookupResult
import com.example.englishdictionary.viewmodel.MainViewModel
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var tts: TextToSpeech? = null
    private var currentWord: String? = null

    companion object {
        const val EXTRA_WORD = "extra_word"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }

        binding.buttonSearch.setOnClickListener {
            performSearch(binding.editWord.text.toString().trim())
        }

        binding.buttonSpeak.setOnClickListener {
            currentWord?.let { word ->
                tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

        binding.buttonFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        viewModel.loading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.buttonSearch.isEnabled = !loading
        }

        viewModel.result.observe(this) { result ->
            renderResult(result)
        }

        viewModel.isFavorite.observe(this) { fav ->
            binding.buttonFavorite.text = if (fav) getString(R.string.action_remove_favorite)
            else getString(R.string.action_add_favorite)
        }

        handleIncomingWord(intent?.getStringExtra(EXTRA_WORD))
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingWord(intent.getStringExtra(EXTRA_WORD))
    }

    private fun handleIncomingWord(word: String?) {
        if (!word.isNullOrBlank()) {
            binding.editWord.setText(word)
            performSearch(word)
        }
    }

    private fun performSearch(word: String) {
        if (word.isBlank()) {
            Toast.makeText(this, getString(R.string.msg_enter_word), Toast.LENGTH_SHORT).show()
            return
        }
        binding.resultCard.visibility = View.GONE
        binding.textError.visibility = View.GONE
        viewModel.search(word)
    }

    private fun renderResult(result: LookupResult) {
        when (result) {
            is LookupResult.Success -> {
                currentWord = result.word
                binding.resultCard.visibility = View.VISIBLE
                binding.textError.visibility = View.GONE

                binding.textWord.text = result.word
                binding.textPhonetic.text = result.phonetic ?: ""

                binding.textPartOfSpeech.text = result.partOfSpeech?.let {
                    getString(R.string.label_part_of_speech, it)
                } ?: ""

                binding.textDefinitionEn.text = getString(R.string.label_definition_en, result.definitionEn)
                binding.textDefinitionKo.text = getString(R.string.label_definition_ko, result.koreanMeaning)

                val example = result.example
                if (example.isNullOrBlank()) {
                    binding.textExample.visibility = View.GONE
                } else {
                    binding.textExample.visibility = View.VISIBLE
                    binding.textExample.text = getString(R.string.label_example, example)
                }
            }

            is LookupResult.NotFound -> {
                binding.resultCard.visibility = View.GONE
                binding.textError.visibility = View.VISIBLE
                binding.textError.text = getString(R.string.msg_not_found, binding.editWord.text.toString())
            }

            is LookupResult.Error -> {
                binding.resultCard.visibility = View.GONE
                binding.textError.visibility = View.VISIBLE
                binding.textError.text = result.message
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
