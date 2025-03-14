package it.polito.g08

import com.uber.h3core.util.LatLng

fun main() {
    val points = Waypoint.fromCSV("/waypoints.csv")
    println("maxDistanceFromStart: ${points.maxDistanceFromStart()}")
    println("waypointsOutsideGeofence: ${points.waypointsOutsideGeofence(
        LatLng(45.041899, 7.649921),
        1.0
    )}")
    val res = points.mostFrequentedArea(1.0)
    println("mostFrequentedArea: $res")
    println("Cittadella design: ${points.pointsInArea(LatLng(res.first.latitude, res.first.longitude), 1.0)}")
    println("mostFrequentedAreaPrecise: ${points.mostFrequentedAreaPrecise(1.0)}")
    //println(points)
}
