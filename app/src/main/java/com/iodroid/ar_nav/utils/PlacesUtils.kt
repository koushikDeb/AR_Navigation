package com.iodroid.ar_nav.utils

import android.content.Context
import android.graphics.Bitmap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.libraries.places.api.model.Place
import com.google.maps.GeoApiContext
import com.iodroid.ar_nav.R

object PlacesUtils {
    val placesRequiredElmentList = listOf(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME)

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

    fun Polyline.addInfoWindow(map: GoogleMap?, title: String, message: String) {
        val pointsOnLine = this.points.size
        val infoLatLng = this.points[(pointsOnLine / 2)]
        val invisibleMarker =
            BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
        val marker = map?.addMarker(
            MarkerOptions()
                .position(infoLatLng)
                .title(title)
                .snippet(message)
                .alpha(0f)
                .icon(invisibleMarker)
                .anchor(0f, 0f)
        )
        marker?.showInfoWindow()
    }

    fun List<com.google.maps.model.LatLng>.toLatLang(): List<LatLng> {
        return this.map { latLang ->
            LatLng(latLang.lat, latLang.lng)
        }
    }
}
