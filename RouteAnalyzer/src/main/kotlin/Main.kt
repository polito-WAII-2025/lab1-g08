package it.polito.g08

import com.uber.h3core.util.LatLng
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.encodeToStream
import mu.KotlinLogging
import org.slf4j.event.Level
import java.io.File

val logger = KotlinLogging.logger {}

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    logger.atLevel(Level.DEBUG)
    try {
        val handleNullability: (String) -> Waypoint =
            { msg -> logger.info {msg}
                Waypoint(0.0, 0.0, 0.0, 0)
            }
        val points = Waypoint.fromCSV("evaluation/waypoints.csv")
        val parameters = CustomParameters.fromYAML("evaluation/custom-parameters.yml", points)
        val maxDistanceFromStartPair = points.maxDistanceFromStart(parameters.earthRadiusKm).let {
            it ?: (handleNullability("Cannot compute max distance, using default") to 0.0)
        }
        val mostFrequentedAreaPair = points.mostFrequentedArea(parameters.mostFrequentedAreaRadiusKm).let {
            it ?: (handleNullability("cannot compute most frequent area, using default") to 0)
        }
        val waypointsNumberOutsideGeofence = points.waypointsOutsideGeofence(
            LatLng(parameters.geofenceCenterLatitude, parameters.geofenceCenterLongitude),
            parameters.geofenceRadiusKm,
            parameters.earthRadiusKm
        )
        val json = mapOf(
            "maxDistanceFromStart" to PathInfo(
                waypoint = maxDistanceFromStartPair.first,
                areaOrDistance = maxDistanceFromStartPair.second,
                firstParamName = "waypoint",
                secondParamName = "distanceKm",
            ), "mostFrequentedArea" to PathInfo(
                waypoint = mostFrequentedAreaPair.first,
                areaOrDistance = parameters.mostFrequentedAreaRadiusKm,
                count = mostFrequentedAreaPair.second,
                firstParamName = "centralWaypoint",
                secondParamName = "areaRadiusKm",
                thirdParamName = "entriesCount",
            ), "waypointsOutsideGeofence" to PathInfo(
                waypoint = Waypoint(
                    0.0,
                    parameters.geofenceCenterLatitude,
                    parameters.geofenceCenterLongitude
                ),
                areaOrDistance = parameters.geofenceRadiusKm,
                count = waypointsNumberOutsideGeofence.size,
                waypoints = waypointsNumberOutsideGeofence,
                firstParamName = "centralWaypoint",
                secondParamName = "areaRadiusKm",
                thirdParamName = "count",
                fourthParamName = "waypoints",
            )
        )
        File("evaluation/output.json").outputStream().use { outputStream ->
            Json.encodeToStream(json, outputStream)
        }

        val jsonAdvanced = mapOf(
            "stopPoints" to Json.encodeToJsonElement(points.getStops()),
            "distanceByPoints" to Json.encodeToJsonElement(points.distanceTravelledByDistancePoints(parameters.earthRadiusKm)),
            "distanceAsCrowFlies" to Json.encodeToJsonElement(points.distanceTravelledByArea(parameters.earthRadiusKm))
        )
        File("evaluation/output-advanced.json").outputStream().use { outputStream ->
            Json.encodeToStream(jsonAdvanced, outputStream)
        }
        logger.info { "Computation completed" }
    } catch (e: Exception) {
        logger.error { "An error occurred: $e" }
    }
}
