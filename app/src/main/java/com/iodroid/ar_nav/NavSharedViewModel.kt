package com.iodroid.ar_nav

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.Polyline
import com.google.android.libraries.places.api.model.Place
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.EncodedPolyline

class NavSharedViewModel : ViewModel() {

    var startPlace: MutableLiveData<Place?> = MutableLiveData(null)
        private set

    var orientation: MutableLiveData<Int> = MutableLiveData(1)
        private set

    var endPlace: MutableLiveData<Place?> = MutableLiveData(null)
        private set

    var availableRoutes: MutableLiveData<MutableList<Polyline>?> = MutableLiveData(mutableListOf())
        private set

    var chosenRoute: MutableLiveData<DirectionsRoute> = MutableLiveData()
        private set

    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

    fun setStart(start: Place) {
        startPlace.value = start
    }

    fun setEnd(end: Place) {
        endPlace.value = end
    }

    fun setOrientation(orient: Int) {
        orientation.value = orient
    }

    fun setAvailableRoutes(polyline: Polyline?) {
        polyline?.let { poly ->
            val currentPolylineList = availableRoutes.value
            currentPolylineList?.add(poly)
            availableRoutes.postValue(currentPolylineList)
        }
    }

    fun setChosenRoute(route: DirectionsRoute) {
        chosenRoute.postValue(route)
    }
}
