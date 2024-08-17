package com.dhiwise.figma.apiController

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.dhiwise.figma.ui.viewmodel.FigmaNodeModel

sealed class ApiResults {
    data class Success(val data: FigmaNodeModel) : ApiResults()
    data class Error(val message: String) : ApiResults()
    data object Loading : ApiResults()
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
