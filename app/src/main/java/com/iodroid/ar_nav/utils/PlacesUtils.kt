package com.iodroid.ar_nav.utils

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.maps.GeoApiContext
import com.iodroid.ar_nav.R

object PlacesUtils {
    val placesRequiredElmentList = listOf(Place.Field.LAT_LNG, Place.Field.NAME)

    fun getGeoContext(context: Context): GeoApiContext? {
        return GeoApiContext.Builder()
            .apiKey(context.getString(R.string.api_key))
            .build()
    }

    fun getStringFormattedLatLang(place: Place?): String {
        place?.let { placeValue ->
            val latLang = placeValue.latLng
            return "${latLang?.latitude}, ${latLang?.longitude}"
        } ?: kotlin.run { return "" }
    }

    fun List<com.google.maps.model.LatLng>.toLatLang(): List<LatLng> {
        return this.map { latLang ->
            LatLng(latLang.lat, latLang.lng)
        }
    }
}
