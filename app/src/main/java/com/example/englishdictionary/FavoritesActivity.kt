package com.example.englishdictionary

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishdictionary.adapter.FavoriteAdapter
import com.example.englishdictionary.data.AppDatabase
import com.example.englishdictionary.databinding.ActivityFavoritesBinding
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var adapter: FavoriteAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = AppDatabase.getInstance(this)

        adapter = FavoriteAdapter(
            items = mutableListOf(),
            onClick = { favorite ->
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra(MainActivity.EXTRA_WORD, favorite.word)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)
                finish()
            },
            onDelete = { favorite ->
                lifecycleScope.launch {
                    db.favoriteDao().delete(favorite)
                    loadFavorites()
                }
            }
        )

        binding.recyclerFavorites.layoutManager = LinearLayoutManager(this)
        binding.recyclerFavorites.adapter = adapter

        loadFavorites()
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            val list = db.favoriteDao().getAll()
            adapter.setItems(list)
            binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
