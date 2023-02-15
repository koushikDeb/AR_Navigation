package com.iodroid.ar_nav

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.Place
import com.google.maps.DirectionsApi
import com.google.maps.model.TrafficModel
import com.iodroid.ar_nav.utils.PlacesUtils
import com.iodroid.ar_nav.utils.PlacesUtils.addInfoWindow
import com.iodroid.ar_nav.utils.PlacesUtils.getColour
import com.iodroid.ar_nav.utils.PlacesUtils.getStringFormattedLatLang
import com.iodroid.ar_nav.utils.PlacesUtils.toLatLang

class MapsFragment : Fragment(R.layout.fragment_maps) {

    private val viewModel: NavSharedViewModel by activityViewModels()
    private var googleMap: GoogleMap? = null
    private var prevPolyline: Polyline? = null

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        viewModel.startPlaceLivedata.value?.let { start ->
            setMarker(start)
        }
        viewModel.endPlaceLiveData.value?.let { end ->
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        locationSetListeners()
    }

    private fun locationSetListeners() {
        viewModel.startPlaceLivedata.observe(viewLifecycleOwner) { start ->
            start?.let { setMarker(it) }
        }
        viewModel.endPlaceLiveData.observe(viewLifecycleOwner) { end ->
            end?.let {
                setMarker(it)

                if (viewModel.startPlaceLivedata.value != null) {
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
                getStringFormattedLatLang(viewModel.startPlaceLivedata.value),
                getStringFormattedLatLang(viewModel.endPlaceLiveData.value)
            ).alternatives(true).departureTimeNow().trafficModel(TrafficModel.BEST_GUESS)
                .optimizeWaypoints(true)
            try {
                val routes = directionsRequest.await().routes

                val routesSorted = routes.sortedBy { route ->
                    route.legs.firstOrNull()?.durationInTraffic?.inSeconds
                }

                viewModel.setChosenRoute(routesSorted.firstOrNull())

                routesSorted.forEachIndexed { index, directionsRoute ->
                    val polyline = directionsRoute.overviewPolyline.decodePath().toLatLang()
                    val colour = getColour(index)
                    val polylineResponse = googleMap?.addPolyline(
                        PolylineOptions().addAll(polyline).color(colour).clickable(true)
                    )
                    if (index == 0) polylineResponse?.addInfoWindow(
                        googleMap,
                        directionsRoute?.legs?.firstOrNull()?.durationInTraffic.toString(),
                        directionsRoute?.legs?.firstOrNull()?.distance.toString()
                    )
                    viewModel.setPolylinesMarked(
                        polyline = polylineResponse,
                        route = directionsRoute
                    )
                }

                polyLineClickListener()
            } catch (e: Exception) {
                Log.e("Directions request exception", e.message.orEmpty())
            }
        }
    }

    private fun polyLineClickListener() = googleMap?.setOnPolylineClickListener { chosenPolyline ->
        if (prevPolyline == null || prevPolyline != chosenPolyline) {
            prevPolyline = chosenPolyline

            val routesPolyLinesPair = viewModel.plottedPolylineRoutesLiveData.value
            val polyLinesList = routesPolyLinesPair?.map { pair ->
                pair.first
            }
            val polylines = polyLinesList?.filter { poly ->
                poly != chosenPolyline
            }
            polylines?.forEach { poly ->
                poly.color = Color.GRAY
            }
            chosenPolyline.color = Color.BLUE

            val chosenRoute = routesPolyLinesPair?.first { pair ->
                pair.first == chosenPolyline
            }?.second

            viewModel.setChosenRoute(chosenRoute)

            chosenPolyline.addInfoWindow(
                googleMap,
                chosenRoute?.legs?.firstOrNull()?.durationInTraffic.toString(),
                chosenRoute?.legs?.firstOrNull()?.distance.toString()
            )
        }
    }
}
