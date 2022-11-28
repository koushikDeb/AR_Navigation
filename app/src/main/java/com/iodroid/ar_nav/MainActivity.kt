package com.iodroid.ar_nav

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.iodroid.ar_nav.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPlaces()
        initListeners()
    }

    private fun initPlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.api_key), Locale.US)
        }
    }

    private fun initListeners() {
        binding.startLocation.setOnClickListener {
            startActivityForResult(getAutoCompleteIntent(), 20)
        }
        binding.endLocation.setOnClickListener {
            startActivityForResult(getAutoCompleteIntent(), 21)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 20) {
            handleResult(resultCode, data)?.let { place ->
                populateStartResult(place)
            }
        } else if (requestCode == 21) {
            handleResult(resultCode, data)?.let { place ->
                populateEndResult(place)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun populateStartResult(place: Place) {
        binding.startLocationTv.text = place.name
    }

    private fun populateEndResult(place: Place) {
        binding.endLocationTv.text = place.name
    }

    private fun getAutoCompleteIntent() =
        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
}
