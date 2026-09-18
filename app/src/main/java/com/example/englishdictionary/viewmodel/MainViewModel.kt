package com.example.englishdictionary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.englishdictionary.data.AppDatabase
import com.example.englishdictionary.data.FavoriteWord
import com.example.englishdictionary.repository.DictionaryRepository
import com.example.englishdictionary.repository.LookupResult
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = DictionaryRepository(db.cacheDao())

    private val _result = MutableLiveData<LookupResult>()
    val result: LiveData<LookupResult> = _result

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun search(word: String) {
        if (word.isBlank()) return
        viewModelScope.launch {
            _loading.value = true
            val res = repository.lookup(word)
            _result.value = res
            if (res is LookupResult.Success) {
                _isFavorite.value = db.favoriteDao().isFavorite(res.word)
            }
            _loading.value = false
        }
    }

    fun toggleFavorite() {
        val current = _result.value
        if (current is LookupResult.Success) {
            viewModelScope.launch {
                if (_isFavorite.value == true) {
                    db.favoriteDao().delete(
                        FavoriteWord(
                            word = current.word,
                            meaningKo = current.koreanMeaning,
                            meaningEn = current.definitionEn
                        )
                    )
                    _isFavorite.value = false
                } else {
                    db.favoriteDao().insert(
                        FavoriteWord(
                            word = current.word,
                            meaningKo = current.koreanMeaning,
                            meaningEn = current.definitionEn
                        )
                    )
                    _isFavorite.value = true
                }
            }
        }
    }
}
