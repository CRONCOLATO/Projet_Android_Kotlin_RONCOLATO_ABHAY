package com.example.myapplication.network

import java.io.Serializable

data class Country(
    val name: String,
    val nameInFrench: String,
    val flagUrl: String,
    val capital: String,
    val officialName: String,
    val population: Int,
    val populationDate: String,
    val continents: List<String>,
    val startOfWeek: String,
    val currency: Currency
) : Serializable

data class Currency(
    val code : String,
    val name: String,
    val symbol: String
) : Serializable


