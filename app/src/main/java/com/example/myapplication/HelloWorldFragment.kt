package com.example.myapplication

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.CountryAdapter
import com.example.myapplication.viewmodel.HelloWorldViewModel
import java.util.Locale

class HelloWorldFragment : Fragment() {

    private val viewModel: HelloWorldViewModel by viewModels()
    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hello_world, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val statusTextView = view.findViewById<TextView>(R.id.statusTextView)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val retryButton = view.findViewById<Button>(R.id.retryButton)
        val voiceSearchButton = view.findViewById<ImageButton>(R.id.voiceSearchButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CountryAdapter(emptyList()) { country ->
            val bundle = Bundle().apply {
                putSerializable("country", country)
            }
            findNavController().navigate(R.id.action_helloWorldFragment_to_countryDetailFragment, bundle)
        }
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.countries.observe(viewLifecycleOwner, Observer { countries ->
            progressBar.visibility = View.GONE
            if (countries.isNotEmpty()) {
                adapter.updateData(countries)
                statusTextView.visibility = View.GONE
                retryButton.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                statusTextView.text = "Les Datas n'ont pas pu être téléchargées"
                statusTextView.visibility = View.VISIBLE
                retryButton.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        })

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                "telechargement..." -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    statusTextView.visibility = View.GONE
                    retryButton.visibility = View.GONE
                }
                "Datas telechargées" -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    statusTextView.visibility = View.GONE
                    retryButton.visibility = View.GONE
                }
                else -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    statusTextView.text = status
                    statusTextView.visibility = View.VISIBLE
                    retryButton.visibility = View.VISIBLE
                }
            }
        })

        retryButton.setOnClickListener {
            viewModel.fetchCountries(true) // Re-fetch countries when retry button is clicked
        }

        voiceSearchButton.setOnClickListener {
            startVoiceInput()
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dites un nom de pays ou de capitale")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == android.app.Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result != null && result.isNotEmpty()) {
                val spokenText = result[0]
                val searchEditText = view?.findViewById<EditText>(R.id.searchEditText)
                searchEditText?.setText(spokenText)
            }
        }
    }
}
