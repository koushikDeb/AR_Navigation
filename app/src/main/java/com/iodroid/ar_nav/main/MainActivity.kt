package com.iodroid.ar_nav.main

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.iodroid.ar_nav.NavSharedViewModel
import com.iodroid.ar_nav.R
import com.iodroid.ar_nav.databinding.ActivityMainBinding
import com.iodroid.ar_nav.utils.isLocationPermissionGranted

class MainActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityMainBinding
    internal val viewModel: NavSharedViewModel by viewModels()
    lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    val locationPermissionList = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel
        viewModel.setOrientation(this.resources.configuration.orientation)

        if (isLocationPermissionGranted(this)) {
            initPlaces()
            initListeners()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    initPlaces()
                    initListeners()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Toast.makeText(this, getString(R.string.fine_loc_message), Toast.LENGTH_LONG)
                        .show()
                    createDialog(
                        context = this,
                        title = getString(R.string.permission_not_granted),
                        desc = getString(R.string.fine_loc_message)
                    )
                }
                else -> {
                    Toast.makeText(this, getString(R.string.fine_loc_message), Toast.LENGTH_LONG)
                        .show()

                    createDialog(
                        context = this,
                        title = getString(R.string.permission_not_granted),
                        desc = getString(R.string.fine_loc_message)
                    )
                }
            }
        }

        locationPermissionRequest.launch(
            locationPermissionList
        )
    }

    private fun createDialog(context: Context, title: String, desc: String) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(desc)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.grant_permission)) { currentDialog, _ ->
                currentDialog.cancel()
                locationPermissionRequest.launch(
                    locationPermissionList
                )
            }
            .setNegativeButton(getString(R.string.deny)) { _, _ ->
                finish()
            }.create()

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.setOrientation(this.resources.configuration.orientation)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    internal fun getAutoCompleteIntent() =
        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, viewModel.fields)
            .build(this)
}
