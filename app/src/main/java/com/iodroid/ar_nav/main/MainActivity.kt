package com.iodroid.ar_nav.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.iodroid.ar_nav.NavSharedViewModel
import com.iodroid.ar_nav.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityMainBinding
    internal val viewModel: NavSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPlaces()
        initListeners()
        binding.viewModel = viewModel
        viewModel.setOrientation(this.resources.configuration.orientation)
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
