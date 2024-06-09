package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityCountryDetailsBinding

class CountryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCountryDetailsBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuration du NavHostFragment et du NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_country_detail) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.helloWorldFragment -> {
                    navController.navigate(R.id.action_countryDetailFragment_to_helloWorldFragment)
                    true
                }
                R.id.favoriteFragment -> {
                    navController.navigate(R.id.action_countryDetailFragment_to_favoriteFragment)
                    true
                }
                R.id.supFragment -> {
                    navController.navigate(R.id.action_countryDetailFragment_to_supFragment)
                    true
                }
                else -> false
            }
        }
    }
}
