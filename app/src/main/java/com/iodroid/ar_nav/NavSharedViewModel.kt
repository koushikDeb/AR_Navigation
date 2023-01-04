package com.iodroid.ar_nav

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.model.Place

class NavSharedViewModel: ViewModel() {

    var startPlace: MutableLiveData<Place?>  = MutableLiveData(null)
        private set

    var orientation: MutableLiveData<Int>  = MutableLiveData(1)
        private set

    var endPlace: MutableLiveData<Place?>  = MutableLiveData(null)
        private set

    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

    fun setStart(start: Place){
        startPlace.value = start
    }

    fun setEnd(end: Place){
        endPlace.value = end
    }

    fun setOrientation(orint:Int){
        orientation.value = orint
    }

}