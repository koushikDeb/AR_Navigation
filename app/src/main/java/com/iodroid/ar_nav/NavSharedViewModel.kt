package com.iodroid.ar_nav

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.Polyline
import com.google.android.libraries.places.api.model.Place
import com.google.maps.model.DirectionsRoute

class NavSharedViewModel : ViewModel() {

    var startPlaceLivedata: MutableLiveData<Place?> = MutableLiveData(null)
        private set

    var orientation: MutableLiveData<Int> = MutableLiveData(1)
        private set

    var endPlaceLiveData: MutableLiveData<Place?> = MutableLiveData(null)
        private set

    var plottedPolylineRoutesLiveData: MutableLiveData<MutableList<Pair<Polyline, DirectionsRoute>>?> =
        MutableLiveData(mutableListOf())
        private set

    var chosenRouteLiveData: MutableLiveData<DirectionsRoute> = MutableLiveData()
        private set

    fun setStart(start: Place) {
        startPlaceLivedata.value = start
    }

    fun setEnd(end: Place) {
        endPlaceLiveData.value = end
    }

    fun setOrientation(orient: Int) {
        orientation.value = orient
    }

    fun setPolylinesMarked(polyline: Polyline?, route: DirectionsRoute) {
        polyline?.let { poly ->
            val currentPolylineList = plottedPolylineRoutesLiveData.value
            currentPolylineList?.add(Pair(poly, route))
            plottedPolylineRoutesLiveData.postValue(currentPolylineList)
        }
    }

    fun setChosenRoute(route: DirectionsRoute?) {
        route?.let { chosenRoute ->
            chosenRouteLiveData.postValue(chosenRoute)
        }
    }
}
