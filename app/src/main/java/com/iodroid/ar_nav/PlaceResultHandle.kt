package com.iodroid.ar_nav

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity

fun MainActivity.handleResult(resultCode: Int, data: Intent?): Place? {
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
