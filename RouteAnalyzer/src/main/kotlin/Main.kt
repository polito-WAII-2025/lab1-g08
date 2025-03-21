package it.polito.g08

import com.uber.h3core.util.LatLng
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    val waypointDefault = Waypoint(0.0, 0.0, 0.0, 0)
    val points = Waypoint.fromCSV("assets/waypoints.csv")
 	val parameters = CustomParameters.fromYAML("assets/custom-parameters.yml", points)
    val maxDistanceFromStartPair = points.maxDistanceFromStart(parameters.earthRadiusKm)
    val mostFrequentedAreaPair = points.mostFrequentedArea(parameters.mostFrequentedAreaRadiusKm)
    val wayPointsNumberOutsideGeofence = points.waypointsOutsideGeofence(
        LatLng(parameters.geofenceCenterLatitude, parameters.geofenceCenterLongitude),
        parameters.geofenceRadiusKm,
        parameters.earthRadiusKm
    )
    val json = mapOf(
        "maxDistanceFromStart" to PathInfo(
            waypoint = maxDistanceFromStartPair?.first ?: waypointDefault,
            areaOrDistance = maxDistanceFromStartPair?.second ?: 0.0,
            firstParamName = "waypoint",
            secondParamName = "distanceKm",
        ), "mostFrequentedArea" to PathInfo(
            waypoint = mostFrequentedAreaPair.first,
            areaOrDistance = parameters.mostFrequentedAreaRadiusKm,
            count = mostFrequentedAreaPair.second,
            firstParamName =  "centralWaypoint",
            secondParamName = "areaRadiusKm",
            thirdParamName = "entriesCount",
        ), "waypointsOutsideGeofence" to PathInfo(
            waypoint = Waypoint(
                0.0,
                parameters.geofenceCenterLatitude,
                parameters.geofenceCenterLongitude
            ),
            areaOrDistance = parameters.geofenceRadiusKm,
            count = wayPointsNumberOutsideGeofence,
            waypoints = listOf(), //TODO change to the waypoints outside the geofence
            firstParamName = "centralWaypoint",
            secondParamName = "areaRadiusKm",
            thirdParamName = "count",
            fourthParamName = "waypoints",
        )
    )
    File("assets/output.json").outputStream().use { outputStream ->
        Json.encodeToStream(json, outputStream)
    }
}
