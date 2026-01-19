package com.example.islamicapp.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object QiblaMath {
    private const val KAABA_LAT = 21.422487
    private const val KAABA_LNG = 39.826206

    /** يرجع (اتجاه القبلة بالدرجات، المسافة بالكيلومتر) */
    fun bearingAndDistanceToKaaba(lat: Double, lng: Double): Pair<Double, Double> {
        val phi1 = Math.toRadians(lat)
        val phi2 = Math.toRadians(KAABA_LAT)
        val dLambda = Math.toRadians(KAABA_LNG - lng)

        val y = sin(dLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(dLambda)
        val bearing = (Math.toDegrees(atan2(y, x)) + 360.0) % 360.0

        // Haversine
        val r = 6371.0
        val dPhi = Math.toRadians(KAABA_LAT - lat)
        val dLam = Math.toRadians(KAABA_LNG - lng)
        val a = sin(dPhi / 2) * sin(dPhi / 2) + cos(phi1) * cos(phi2) * sin(dLam / 2) * sin(dLam / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val dist = r * c
        return bearing to dist
    }
}
