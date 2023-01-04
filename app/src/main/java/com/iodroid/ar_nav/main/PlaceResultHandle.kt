package com.iodroid.ar_nav.main

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.iodroid.ar_nav.main.MainActivity

const val START_REQUEST_CODE = 20
const val END_REQUEST_CODE = 21

fun handleResult(resultCode: Int, data: Intent?): Place? {
    when (resultCode) {
        AppCompatActivity.RESULT_OK -> {
            data?.let {
                val place = Autocomplete.getPlaceFromIntent(data)
                Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}")
                return place
            }
        }
        AutocompleteActivity.RESULT_ERROR, AppCompatActivity.RESULT_CANCELED -> {
            data?.let {
                val status = Autocomplete.getStatusFromIntent(data)
                Log.i(ContentValues.TAG, status.statusMessage ?: "")
                return null
            }
        }
    }
    return null
}

fun MainActivity.onResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
) {
    if (requestCode == START_REQUEST_CODE) {
        handleResult(resultCode, data)?.let { place ->
            viewModel.setStart(place)
        }
    } else if (requestCode == END_REQUEST_CODE) {
        handleResult(resultCode, data)?.let { place ->
            viewModel.setEnd(place)
        }
    }
}