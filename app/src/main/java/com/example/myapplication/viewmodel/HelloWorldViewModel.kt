package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.Country
import com.example.myapplication.repository.CountryRepository
import kotlinx.coroutines.launch

class HelloWorldViewModel : ViewModel() {

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>> get() = _countries

    private val _loadingStatus = MutableLiveData<String>()
    val loadingStatus: LiveData<String> get() = _loadingStatus

    init {
        fetchCountries()
    }

    fun fetchCountries(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _loadingStatus.postValue("telechargement...")
            val result = CountryRepository.getCountries(forceRefresh)
            _countries.postValue(result)
            _loadingStatus.postValue(if (result.isNotEmpty() && !result[0].name.startsWith("Error")) "Datas telechargées" else "Les Datas n'ont pas pu être téléchargées")
        }
    }
}
