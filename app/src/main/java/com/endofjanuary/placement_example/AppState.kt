/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.ui

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * List of screens for [JetcasterApp]
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ThreeD : Screen("threed_screen/{prompt}") {
        fun createRoute(prompt: String) = "player/$prompt"
    }
    object AR : Screen("ar_screen") {
       // fun createRoute() = "player/$episodeUri"
    }
}

@Composable
fun rememberARExampleAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    ARExampleAppState(navController, context)
}

class ARExampleAppState(
    val navController: NavHostController,
    private val context: Context
) {
//    var isOnline by mutableStateOf(checkIfOnline())
//        private set
//
//    fun refreshOnline() {
//        isOnline = checkIfOnline()
//    }

    fun navigateToScreen(episodeUri: String, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            val encodedUri = Uri.encode(episodeUri)
            navController.navigate(Screen.ThreeD.createRoute(encodedUri))
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

//    @Suppress("DEPRECATION")
//    private fun checkIfOnline(): Boolean {
//        val cm = getSystemService(context, ConnectivityManager::class.java)
//
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
//            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
//                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
//        } else {
//            cm?.activeNetworkInfo?.isConnectedOrConnecting == true
//        }
//    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
