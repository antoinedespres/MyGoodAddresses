package com.despreschen.mygoodaddresses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.despreschen.mygoodaddresses.ui.AppNavHost
import com.despreschen.mygoodaddresses.ui.theme.MyGoodAddressesTheme

/**
 * The app's only Activity.
 *
 * The former splash, list, add and map Activities are Compose destinations. The
 * splash is handled by the platform API, which removes the artificial 900 ms
 * delay the old SplashScreenActivity imposed on every launch.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MyGoodAddressesTheme {
                AppNavHost()
            }
        }
    }
}
