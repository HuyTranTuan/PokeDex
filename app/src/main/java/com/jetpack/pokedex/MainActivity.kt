package com.jetpack.pokedex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.*
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.commitNow
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jetpack.pokedex.data.source.OkHttpApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.coroutines.async
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.fragment.app.FragmentActivity

class MainActivity : androidx.fragment.app.FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(3000)
            keepSplashScreen = false
        }
        enableEdgeToEdge() // set fullscreen

        lifecycleScope.launch { // Launch a coroutine in the activity's lifecycle scope
            setContentView(R.layout.activity_main)
            if (savedInstanceState == null || !supportFragmentManager.isStateSaved) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, MainPokedexFragment())
                    .commitNow()
            }
        }
    }
}
