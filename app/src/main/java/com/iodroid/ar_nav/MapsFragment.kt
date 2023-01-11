package com.iodroid.ar_nav

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.Place
import com.google.maps.DirectionsApi
import com.iodroid.ar_nav.utils.PlacesUtils
import com.iodroid.ar_nav.utils.PlacesUtils.getStringFormattedLatLang
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
            ).alternatives(true).optimizeWaypoints(true)
            try {
                val routes = directionsRequest.await().routes

                for (route in routes) {
                    val polyline = route.overviewPolyline.decodePath().map { latLang ->
                        LatLng(latLang.lat, latLang.lng)
                    }
                    val colour = Color.argb(
                        255,
                        Random.nextInt(256),
                        Random.nextInt(256),
                        Random.nextInt(256)
                    )

                    googleMap?.addPolyline(
                        PolylineOptions()
                            .addAll(polyline)
                            .color(colour)
                    )
                }
            } catch (e: Exception) {
                Log.e("Directions request exception", e.message.orEmpty())
            }
        }
    }
}
