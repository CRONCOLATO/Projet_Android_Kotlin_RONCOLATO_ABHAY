package com.example.myapplication.network

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

object ApiServiceCountry {
    private val client = ApiClient.client

    suspend fun fetchCountries(): List<Country> {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.get("https://restcountries.com/v3.1/all")
                val jsonString = response.bodyAsText()
                parseCountries(jsonString)
            } catch (e: Exception) {
                listOf(
                    Country(
                        name = "Error: ${e.message}",
                        nameInFrench = "",
                        flagUrl = "",
                        capital = "",
                        officialName = "",
                        population = 0,
                        populationDate = "",
                        continents = emptyList(),
                        startOfWeek = "",
                        currency = Currency("", "", "")
                    )
                )
            }
        }
    }

    private fun parseCountries(jsonString: String): List<Country> {
        val json = Json.parseToJsonElement(jsonString)
        return when {
            json is JsonArray -> parseJsonArray(json)
            json is JsonObject -> parseJsonObject(json)
            else -> emptyList()
        }
    }

    private fun parseJsonArray(jsonArray: JsonArray): List<Country> {
        val countries = mutableListOf<Country>()
        for (element in jsonArray) {
            if (element is JsonObject) {
                val name =
                    element.jsonObject["name"]?.jsonObject?.get("common")?.jsonPrimitive?.content
                val nameInFrench = element.jsonObject["translations"]?.jsonObject
                    ?.get("fra")?.jsonObject
                    ?.get("common")?.jsonPrimitive?.content
                val flagUrl =
                    element.jsonObject["flags"]?.jsonObject?.get("png")?.jsonPrimitive?.content
                val population =
                    element.jsonObject["population"]?.jsonPrimitive?.longOrNull?.toInt() ?: 0
                val populationDate =
                    element.jsonObject["gini"]?.jsonObject?.keys?.firstOrNull() ?: ""
                val capital =
                    element.jsonObject["capital"]?.jsonArray?.firstOrNull()?.jsonPrimitive?.content
                        ?: ""
                val continents =
                    element.jsonObject["continents"]?.jsonArray?.mapNotNull { it.jsonPrimitive?.content }
                        ?: emptyList()
                val startOfWeek = element.jsonObject["startOfWeek"]?.jsonPrimitive?.content ?: ""
                val currencyJsonObject = element.jsonObject["currencies"]?.jsonObject
                val currency = parseCurrency(currencyJsonObject)

                if (name != null && flagUrl != null) {
                    countries.add(
                        Country(
                            name = name,
                            nameInFrench = nameInFrench ?: "",
                            flagUrl = flagUrl,
                            capital = capital,
                            officialName = "",
                            population = population,
                            populationDate = populationDate,
                            continents = continents,
                            startOfWeek = startOfWeek,
                            currency = currency
                        )
                    )
                }
            }
        }
        return countries
    }

    private fun parseJsonObject(jsonObject: JsonObject): List<Country> {
        val name = jsonObject["name"]?.jsonObject?.get("common")?.jsonPrimitive?.content
        val nameInFrench = jsonObject["translations"]?.jsonObject
            ?.get("fra")?.jsonObject
            ?.get("common")?.jsonPrimitive?.content
        val flagUrl = jsonObject["flags"]?.jsonObject?.get("png")?.jsonPrimitive?.content
        val population = jsonObject["population"]?.jsonPrimitive?.longOrNull?.toInt() ?: 0
        val populationDate = jsonObject["gini"]?.jsonObject?.keys?.firstOrNull() ?: ""
        val capital = jsonObject["capital"]?.jsonArray?.firstOrNull()?.jsonPrimitive?.content ?: ""
        val continents =
            jsonObject["continents"]?.jsonArray?.mapNotNull { it.jsonPrimitive?.content }
                ?: emptyList()
        val startOfWeek = jsonObject["startOfWeek"]?.jsonPrimitive?.content ?: ""
        val currencyJsonObject = jsonObject["currencies"]?.jsonObject
        val currency = parseCurrency(currencyJsonObject)

        return if (name != null && flagUrl != null) {
            listOf(
                Country(
                    name = name,
                    nameInFrench = nameInFrench ?: "",
                    flagUrl = flagUrl,
                    capital = capital,
                    officialName = "",
                    population = population,
                    populationDate = populationDate,
                    continents = continents,
                    startOfWeek = startOfWeek,
                    currency = currency
                )
            )
        } else {
            emptyList()
        }
    }

    private fun parseCurrency(currencyJsonObject: JsonObject?): Currency {
        return if (currencyJsonObject != null && currencyJsonObject.size > 0) {
            val currencyCode = currencyJsonObject.keys.first()
            val currencyName =
                currencyJsonObject[currencyCode]?.jsonObject?.get("name")?.jsonPrimitive?.content
                    ?: ""
            val currencySymbol =
                currencyJsonObject[currencyCode]?.jsonObject?.get("symbol")?.jsonPrimitive?.content
                    ?: ""
            Currency(currencyCode, currencyName, currencySymbol)
        } else {
            Currency("", "", "")
        }
    }
}
