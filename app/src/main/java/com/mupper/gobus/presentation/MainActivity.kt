package com.mupper.gobus.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mupper.gobus.presentation.ui.app.GobusApp
import com.mupper.gobus.presentation.ui.map.MapFacade
import com.mupper.gobus.presentation.ui.theme.GobusTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.osmdroid.config.Configuration.getInstance

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var mapFacade: MapFacade? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getInstance().load(this, getDefaultSharedPreferences(this))

        setContent {
            GobusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GobusApp {
                        mapFacade = this
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapFacade?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapFacade?.onPause()
    }
}