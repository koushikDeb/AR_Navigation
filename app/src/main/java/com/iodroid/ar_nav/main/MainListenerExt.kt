package com.iodroid.ar_nav.main

import android.util.Log
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.iodroid.ar_nav.R
import com.iodroid.ar_nav.utils.PlacesUtils.placesRequiredElmentList
import com.iodroid.ar_nav.utils.isLocationPermissionGranted
import java.util.*

internal fun MainActivity.initPlaces() {
    if (!Places.isInitialized()) {
        Places.initialize(applicationContext, getString(R.string.api_key), Locale.US)
    }
}

internal fun MainActivity.initListeners() {
    setCurrentUserLocation()

    binding.startLocation.setOnClickListener {
        startActivityForResult(getAutoCompleteIntent(), START_REQUEST_CODE)
    }
    binding.endLocation.setOnClickListener {
        startActivityForResult(getAutoCompleteIntent(), END_REQUEST_CODE)
    }

    viewModel.startPlace.observe(this) { start ->
        binding.startLocationTv.text = start?.name ?: getString(R.string.start_location)
    }
    viewModel.endPlace.observe(this) { end ->
        binding.endLocationTv.text = end?.name ?: getString(R.string.end_location)
    }
}

internal fun MainActivity.setCurrentUserLocation() {
    if (isLocationPermissionGranted(this)) {
        getCurrentLocation()
    }
}

private fun MainActivity.getCurrentLocation() {
    val places = placesRequiredElmentList

    val request = FindCurrentPlaceRequest.newInstance(places)

    val placesClient = Places.createClient(this)

    getCurrentPlaceResponse(placesClient, request)
}

private fun MainActivity.getCurrentPlaceResponse(
    placesClient: PlacesClient,
    request: FindCurrentPlaceRequest?
) {
    val placeResponse = placesClient.findCurrentPlace(request)

    placeResponse.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val mostLikelyPlace = task.result.placeLikelihoods.first().place

            binding.startLocationTv.text = mostLikelyPlace.name
            viewModel.setStart(mostLikelyPlace)
        } else {
            val exception = task.exception
            Log.e("Places Response exception", exception?.message.orEmpty())
        }
    }
}
