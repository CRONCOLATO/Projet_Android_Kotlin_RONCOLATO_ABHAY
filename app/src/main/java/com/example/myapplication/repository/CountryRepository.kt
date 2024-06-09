package com.example.myapplication.repository

import com.example.myapplication.network.ApiServiceCountry
import com.example.myapplication.network.Country

object CountryRepository {
    private var cachedCountries: List<Country>? = null

    suspend fun getCountries(forceRefresh: Boolean = false): List<Country> {
        return if (forceRefresh || cachedCountries == null) {
            val countries = ApiServiceCountry.fetchCountries()
            cachedCountries = countries
            countries
        } else {
            cachedCountries ?: emptyList()
        }
    }
}
