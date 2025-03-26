package it.polito.g08

import com.uber.h3core.AreaUnit
import com.uber.h3core.H3Core
import com.uber.h3core.LengthUnit
import com.uber.h3core.util.LatLng
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.math.*

private object H3Singleton {
    val h3: H3Core = H3Core.newInstance()
}


@Serializable
@DataSchema(isOpen = false)
data class Waypoint(
    val timestamp: Double,
    val latitude: Double,
    val longitude: Double,
    @Transient val cell: Long = 0L
) {
    companion object {
        fun fromCSV(resourcePath: String, cellResolution: Int = 15): List<Waypoint> {
            val h3 = H3Singleton.h3
            val inputStream = Files.newInputStream(Paths.get(resourcePath))
                ?: throw IllegalArgumentException("File not found: $resourcePath")

            return DataFrame.readCSV(
                inputStream,
                header = listOf("timestamp", "latitude", "longitude"),
                delimiter = ';',
                // The parser uses the system's locale to parse numbers and dates, causing problems
                // when parsing Doubles (the CSV uses dots to represent decimals), so we force using
                // the US locale to prevent problems.
                parserOptions = ParserOptions(Locale.US)
            ).add("cell") {
                h3.latLngToCell("latitude"(), "longitude"(), cellResolution)
            }.cast<Waypoint>().toList()
        }
    }
}

/**
 * Finds the furthest waypoint from the path's starting waypoint
 *
 * @param earthRadius the radius of the Earth in Km
 * @return A `Pair` containing the furthest waypoint and its distance from the starting waypoint, or `null` if
 * the `List` does not contain enough waypoints
 */
fun List<Waypoint>.maxDistanceFromStart(earthRadius: Double): Pair<Waypoint, Double>? {
    val h3 = H3Singleton.h3
    val startingPoint = this.firstOrNull()?.let {
        LatLng(it.latitude, it.longitude)
    } ?: return null

    return this.map {
        val currentPoint = LatLng(it.latitude, it.longitude)
        val distance = h3.greatCircleDistance(startingPoint, currentPoint, LengthUnit.rads) * earthRadius
        Pair(it, distance)
    }.maxByOrNull {
        it.second
    }
}

/**
 * Finds the list of points outside a user provided geofence
 *
 * @param geofenceCenter the center of the geofence
 * @param geofenceRadius the radius of the geofence in Km
 * @param earthRadius the radius of the Earth in Km
 * @return List of points outside the geofence
 */
fun List<Waypoint>.waypointsOutsideGeofence(geofenceCenter: LatLng, geofenceRadius: Double, earthRadius: Double): List<Waypoint> {
    val h3 = H3Singleton.h3
    return this.filter {
        val currentPoint = LatLng(it.latitude, it.longitude)
        h3.greatCircleDistance(geofenceCenter, currentPoint, LengthUnit.rads) * earthRadius > geofenceRadius
    }
}

/**
 * Finds the coordinates of the most frequented area.
 * Uses Uber's H3 geospatial indexing system. Provides an approximate result in linear time.
 *
 * @param areaRadius the desired minimum area radius in Km
 * @return A `Pair` containing the coordinates of the most frequented area and the corresponding number of waypoints
 */
fun List<Waypoint>.mostFrequentedArea(areaRadius: Double): Pair<Waypoint, Int>? {
    if (this.size < 2) return null
    val h3 = H3Singleton.h3
    val areaKm2 = Math.PI * areaRadius.pow(2.0)
    val closestResolution = (0..15).reduce { acc, i ->
        val avgHexArea = h3.getHexagonAreaAvg(i, AreaUnit.km2)
        if (avgHexArea > areaKm2)
            i
        else
            return@reduce acc
    }

    return this.groupBy {
        // Each resolution corresponds to 1 Nibble (4 bits) in the cell address
        it.cell and (Long.MAX_VALUE shl ((closestResolution - 1 ) * 4))
    }.map {
        Pair(it.key, it.value.size)
    }.maxBy {
        it.second
    }.let {
        val ret = h3.cellToLatLng(it.first).let { latLng ->
            Waypoint(0.0, latLng.lat, latLng.lng, it.first)
        }
        Pair(ret, it.second)
    }
}

/**
 * Finds the coordinates of the most frequented area.
 * Uses a slow precise search of quadratic time
 *
 * @param areaRadius the desired minimum area radius in Km
 * @param earthRadius the radius of the Earth in Km
 * @return A `Pair` containing the coordinates of the most frequented area and the corresponding number of waypoints
 */
fun List<Waypoint>.mostFrequentedAreaPrecise(areaRadius: Double, earthRadius: Double): Pair<Waypoint, Int> {
    val h3 = H3Singleton.h3
    return this.map { waypoint ->
        val currentPoint = LatLng(waypoint.latitude, waypoint.longitude)
        val outside = this.fold(0) {acc, w ->
            val wp = LatLng(w.latitude, w.longitude)
            if (h3.greatCircleDistance(currentPoint, wp, LengthUnit.rads) * earthRadius > areaRadius)
                acc
            else
                acc + 1
        }
        Pair(waypoint, outside)
    }.maxBy {
        it.second
    }
}

/**
 * Finds how many points are within an area
 *
 * @param point the center of the area
 * @param areaRadius the desired minimum area radius in Km
 * @param earthRadius the radius of the Earth in Km
 * @return The number of points in the area
 */
fun List<Waypoint>.pointsInArea(point: LatLng, areaRadius: Double, earthRadius: Double): Int {
    val h3 = H3Singleton.h3
    return this.fold(0) {acc, w ->
        val wp = LatLng(w.latitude, w.longitude)
        if (h3.greatCircleDistance(point, wp, LengthUnit.rads) * earthRadius > areaRadius)
            acc
        else
            acc + 1
    }
}

fun List<Waypoint>.distanceTravelledByArea(earthRadius: Double): Double {
    if (this.size < 2)
        return 0.0

    val h3 = H3Singleton.h3
    return this.subList(1, this.size).foldIndexed(0.0) { i, acc, w ->
        val wp1 = LatLng(this[i].latitude, this[i].longitude)
        val wp2 = LatLng(w.latitude, w.longitude)
        acc + h3.greatCircleDistance(wp1, wp2, LengthUnit.rads)
    } * earthRadius
}


fun List<Waypoint>.distanceTravelledByDistancePoints(earthRadius: Double): Double {
    if (this.size < 2)
        return 0.0

    val h3 = H3Singleton.h3
    return this.subList(1, this.size).foldIndexed(0.0) { i, acc, w ->
        val wp1 = LatLng(this[i].latitude, this[i].longitude)
        val wp2 = LatLng(w.latitude, w.longitude)
        if (i>1 && (wp1==wp2) && i<this.size-2){
            val wp0 = LatLng(this[i-1].latitude, this[i-1].longitude)
            val waypointsDistance = h3.greatCircleDistance(wp0, wp2, LengthUnit.rads) * earthRadius
            val oldWaypointsDistance = waypointsRealDistance(waypointsDistance)
            acc - oldWaypointsDistance + waypointsDistance
        }else if (i==this.size-2) {
            val waypointsDistance = h3.greatCircleDistance(wp1, wp2, LengthUnit.rads) * earthRadius
            acc + waypointsDistance
        }else {
            val waypointsDistance = waypointsRealDistance(h3.greatCircleDistance(wp1, wp2, LengthUnit.rads) * earthRadius)
            acc + waypointsDistance
        }
    }
}

private fun waypointsRealDistance(areaDistance: Double) : Double {
    return when {
        areaDistance < 0.05 -> 0.0
        areaDistance < 0.5 -> 0.1
        areaDistance < 1.5 -> 1.0
        areaDistance < 2.5 -> 2.0
        areaDistance < 3.5 -> 3.0
        areaDistance < 5.5 -> 5.0
        else -> areaDistance
    }
}

fun List<Waypoint>.getStops() : List<Waypoint> {
    return listOf(this.first()) + this.subList(1, this.size).filterIndexed { i: Int, w: Waypoint ->
        val wp1 = LatLng(this[i].latitude, this[i].longitude)
        val wp2 = LatLng(w.latitude, w.longitude)
        ((i > 1) && (wp1 == wp2) && (i < (this.size - 2)))
    } + this.last()
}
