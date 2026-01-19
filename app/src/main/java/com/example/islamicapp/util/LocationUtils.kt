package com.example.islamicapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

data class CityCountry(
    val city: String,
    val country: String,
    val cityArabic: String = city,
    val countryArabic: String = country
)

object LocationUtils {
    suspend fun getLatLng(context: Context): Pair<Double, Double>? {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) return null

        val client = LocationServices.getFusedLocationProviderClient(context)
        return suspendCancellableCoroutine { cont ->
            client.lastLocation
                .addOnSuccessListener { loc ->
                    cont.resume(if (loc != null) Pair(loc.latitude, loc.longitude) else null)
                }
                .addOnFailureListener { _ -> cont.resume(null) }
        }
    }

    suspend fun getCityAndCountry(context: Context): CityCountry? {
        val latLng = getLatLng(context) ?: return null
        return try {
            val geoAr = Geocoder(context, Locale("ar"))
            val geoEn = Geocoder(context, Locale.ENGLISH)

            val ar = geoAr.getFromLocation(latLng.first, latLng.second, 1)?.firstOrNull()
            val en = geoEn.getFromLocation(latLng.first, latLng.second, 1)?.firstOrNull()

            val cityEn = en?.locality ?: en?.subAdminArea ?: en?.adminArea ?: ""
            val countryEn = en?.countryName ?: ""
            val cityAr = ar?.locality ?: ar?.subAdminArea ?: ar?.adminArea ?: cityEn
            val countryAr = ar?.countryName ?: countryEn

            if (cityEn.isBlank() && countryEn.isBlank()) null
            else CityCountry(city = cityEn.ifBlank { cityAr }, country = countryEn.ifBlank { countryAr }, cityArabic = cityAr, countryArabic = countryAr)
        } catch (_: Throwable) {
            null
        }
    }
}
