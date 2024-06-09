package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.network.Country
import com.example.myapplication.repository.FavoriteRepository

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteRepository = FavoriteRepository(application.applicationContext)
    private val _favoriteCountries = MutableLiveData<List<Country>>()
    val favoriteCountries: LiveData<List<Country>> get() = _favoriteCountries

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        _favoriteCountries.postValue(favoriteRepository.getFavorites())
    }

    fun addFavorite(country: Country) {
        favoriteRepository.addFavorite(country)
        loadFavorites()
    }

    fun removeFavorite(country: Country) {
        favoriteRepository.removeFavorite(country)
        loadFavorites()
    }
}
