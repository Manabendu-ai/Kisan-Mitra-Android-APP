package com.riku.kisanmitra

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.rememberNavController
import com.riku.kisanmitra.navigation.NavGraph
import com.riku.kisanmitra.ui.MainViewModel
import com.riku.kisanmitra.ui.theme.KisanMitraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val selectedLanguage by mainViewModel.selectedLanguage.collectAsState()

            LaunchedEffect(selectedLanguage) {
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(selectedLanguage)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }

            KisanMitraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController, mainViewModel = mainViewModel)
                }
            }
        }
    }
}
