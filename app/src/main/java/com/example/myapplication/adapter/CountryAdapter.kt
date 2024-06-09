package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.network.Country

class CountryAdapter(
    private var countryList: List<Country>,
    private val onItemClick: (Country) -> Unit
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var filteredCountryList: List<Country> = countryList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = filteredCountryList[position]
        holder.bind(country)
    }

    override fun getItemCount(): Int {
        return filteredCountryList.size
    }

    fun updateData(newCountryList: List<Country>) {
        countryList = newCountryList
        filteredCountryList = newCountryList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredCountryList = if (query.isBlank()) {
            countryList
        } else {
            countryList.filter {
                it.nameInFrench.contains(query, ignoreCase = true) ||
                        it.name.contains(query, ignoreCase = true) ||
                        it.capital.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    inner class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val countryNameTextView: TextView = itemView.findViewById(R.id.countryNameTextView)
        private val capitalTextView: TextView = itemView.findViewById(R.id.capitalTextView)
        private val flagImageView: ImageView = itemView.findViewById(R.id.flagImageView)

        fun bind(country: Country) {
            countryNameTextView.text = country.nameInFrench.takeIf { it.isNotEmpty() } ?: country.name
            capitalTextView.text = country.capital.takeIf { it.isNotEmpty() } ?: "Unknown"
            Glide.with(itemView.context).load(country.flagUrl).into(flagImageView)

            itemView.setOnClickListener {
                onItemClick(country)
            }
        }
    }
}
