package com.example.myapplication

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentCountryDetailBinding
import com.example.myapplication.network.Country
import com.example.myapplication.network.ApiServiceCountry
import com.example.myapplication.repository.FavoriteRepository
import kotlinx.coroutines.launch
import java.util.Locale

class CountryDetailFragment : Fragment(), TextToSpeech.OnInitListener {
    private var _binding: FragmentCountryDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoritesRepository: FavoriteRepository
    private lateinit var tts: TextToSpeech
    private var country: Country? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCountryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation du favoritesRepository et Text-to-Speech
        favoritesRepository = FavoriteRepository(requireContext())
        tts = TextToSpeech(requireContext(), this)

        // Récupération des détails du pays depuis les arguments
        country = arguments?.getSerializable("country") as? Country

        country?.let {
            displayCountryDetails(it)
            setupFavoriteButton(it)
        } ?: fetchCountryDetails()

        binding.readAloudButton.setOnClickListener {
            country?.let {
                val text = """
                    Pays : ${it.name}
                    Nom officiel : ${it.officialName}
                    Capitale : ${it.capital}
                    Population : ${it.population}
                    Devise : ${it.currency.code} ${it.currency.name} ${it.currency.symbol}
                    Continent : ${it.continents.joinToString()}
                    Début de la semaine : ${it.startOfWeek}
                """.trimIndent()
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    private fun fetchCountryDetails() {
        lifecycleScope.launch {
            try {
                val countries = ApiServiceCountry.fetchCountries()
                if (countries.isNotEmpty()) {
                    displayCountryDetails(countries[0])
                    setupFavoriteButton(countries[0])
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun displayCountryDetails(country: Country) {
        binding.apply {
            Glide.with(this@CountryDetailFragment)
                .load(country.flagUrl)
                .placeholder(R.drawable.default_flag_image)
                .error(R.drawable.default_flag_image)
                .into(flagImageView)

            countryNameTextView.text = country.nameInFrench.takeIf { it.isNotEmpty() } ?: country.name
            officialNameTextView.text = country.officialName
            capitalTextView.text = "Capitale : ${country.capital}"
            populationTextView.text = "Population : ${country.population} (Pris en ${country.populationDate})"
            continentTextView.text = "Continent : ${country.continents.joinToString()}"
            startOfWeekTextView.text = "Début de la semaine : ${country.startOfWeek}"
            currencyTextView.text = "Devise : ${country.currency.code} (${country.currency.name} - ${country.currency.symbol})"
        }
    }

    private fun setupFavoriteButton(country: Country) {
        val isFavorite = favoritesRepository.getFavorites().contains(country)
        binding.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )

        binding.favoriteButton.setOnClickListener {
            if (isFavorite) {
                favoritesRepository.removeFavorite(country)
                Toast.makeText(requireContext(), "Retiré des favoris", Toast.LENGTH_SHORT).show()
            } else {
                favoritesRepository.addFavorite(country)
                Toast.makeText(requireContext(), "Ajouté aux favoris", Toast.LENGTH_SHORT).show()
            }
            binding.favoriteButton.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_border else R.drawable.ic_favorite
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        tts.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
        } else {
            Toast.makeText(requireContext(), "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show()
        }
    }
}
