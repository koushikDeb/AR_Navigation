package com.iodroid.ar_nav.utils

import com.google.android.libraries.places.api.model.Place

object PlacesUtils {
    val placesRequiredElmentList = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
}