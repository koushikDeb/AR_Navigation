package com.iodroid.ar_nav

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.Place
import com.google.maps.DirectionsApi
import com.google.maps.model.TrafficModel
import com.iodroid.ar_nav.utils.PlacesUtils
import com.iodroid.ar_nav.utils.PlacesUtils.getStringFormattedLatLang
import com.iodroid.ar_nav.utils.PlacesUtils.toLatLang
import kotlin.random.Random

class MapsFragment : Fragment() {

    private val viewModel: NavSharedViewModel by activityViewModels()
    private var googleMap: GoogleMap? = null

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        viewModel.startPlace.value?.let { start ->
            setMarker(start)
        }
        viewModel.endPlace.value?.let { end ->
            setMarker(end)
        }
    }

    private fun setMarker(place: Place) {
        googleMap?.let { map ->
            place.latLng?.let { latLng ->
                place.name?.let { name ->
                    map.addMarker(MarkerOptions().position(latLng).title(name))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        locationSetListeners()
    }

    private fun locationSetListeners() {
        viewModel.startPlace.observe(viewLifecycleOwner) { start ->
            start?.let { setMarker(it) }
        }
        viewModel.endPlace.observe(viewLifecycleOwner) { end ->
            end?.let {
                setMarker(it)

                if (viewModel.startPlace.value != null) {
                    getRoutePolyline()
                }
            }
        }
    }

    private fun getRoutePolyline() {
        val geoContext = PlacesUtils.getGeoContext(context = requireContext())

        geoContext?.let { context ->
            val directionsRequest = DirectionsApi.getDirections(
                context,
                getStringFormattedLatLang(viewModel.startPlace.value),
                getStringFormattedLatLang(viewModel.endPlace.value)
            ).alternatives(true)
                .departureTimeNow()
                .trafficModel(TrafficModel.BEST_GUESS)
                .optimizeWaypoints(true)
            try {
                val routes = directionsRequest.await().routes

                for (route in routes) {
                    val polyline = route.overviewPolyline.decodePath().toLatLang()
                    val colour = Color.argb(
                        255,
                        Random.nextInt(256),
                        Random.nextInt(256),
                        Random.nextInt(256)
                    )

                    val polylineResponse = googleMap?.addPolyline(
                        PolylineOptions()
                            .addAll(polyline)
                            .color(colour)
                            .clickable(true)
                    )
                    viewModel.setAvailableRoutes(polylineResponse)
                }

                googleMap?.setOnPolylineClickListener {
                    val polylines = viewModel.availableRoutes.value?.filter { poly ->
                        poly != it
                    }
                    polylines?.forEach { poly ->
                        poly.color = Color.DKGRAY
                    }
                    it.color = Color.BLUE

                    Log.e("Polyline clicked", it.id)
                }
            } catch (e: Exception) {
                Log.e("Directions request exception", e.message.orEmpty())
            }
        }
    }
}
