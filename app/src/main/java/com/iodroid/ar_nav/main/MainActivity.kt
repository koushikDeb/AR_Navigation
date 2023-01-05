package com.iodroid.ar_nav.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.iodroid.ar_nav.NavSharedViewModel
import com.iodroid.ar_nav.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityMainBinding
    internal val viewModel: NavSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPlaces()
        initListeners()
        binding.viewModel = viewModel
        viewModel.setOrientation(this.resources.configuration.orientation)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val requestPermissionLauncher =
                registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // feature requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                    }
                }
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else{
            val places = listOf(Place.Field.LAT_LNG, Place.Field.NAME)

            val request = FindCurrentPlaceRequest.newInstance(places)

            val placesClient = Places.createClient(this)

            val placeResponse = placesClient.findCurrentPlace(request)

            placeResponse.addOnCompleteListener {task ->
                if (task.isSuccessful){
                    binding.startLocationTv.text = task.result.placeLikelihoods.first().place.name
                    viewModel.setStart(task.result.placeLikelihoods.first().place)
                }else {
                    val exception = task.exception
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.setOrientation(this.resources.configuration.orientation)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    internal fun getAutoCompleteIntent() =
        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, viewModel.fields)
            .build(this)
}
