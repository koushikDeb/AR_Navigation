package com.iodroid.ar_nav.main

import com.google.android.libraries.places.api.Places
import com.iodroid.ar_nav.R
import java.util.*

internal fun MainActivity.initPlaces() {
    if (!Places.isInitialized()) {
        Places.initialize(applicationContext, getString(R.string.api_key), Locale.US)
    }
}

internal fun MainActivity.initListeners() {
    binding.startLocation.setOnClickListener {
        startActivityForResult(getAutoCompleteIntent(), START_REQUEST_CODE)
    }
    binding.endLocation.setOnClickListener {
        startActivityForResult(getAutoCompleteIntent(), END_REQUEST_CODE)
    }


    viewModel.startPlace.observe(this){ start ->
        binding.startLocationTv.text = start?.name?:getString(R.string.start_location)
    }
    viewModel.endPlace.observe(this){ end->
        binding.endLocationTv.text = end?.name?:getString(R.string.end_location)
    }
}