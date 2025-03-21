package it.polito.g08

import org.yaml.snakeyaml.Yaml
import java.io.InputStream

data class CustomParameters(
    val earthRadiusKm: Double,
    val geofenceCenterLatitude: Double,
    val geofenceCenterLongitude: Double,
    val geofenceRadiusKm: Double,
    val mostFrequentedAreaRadiusKm: Double
) {
    companion object {

        fun fromYAML(resourcePath: String, points: List<Waypoint>): CustomParameters {

            val inputStream: InputStream = object {}.javaClass.getResourceAsStream(resourcePath)
                ?: throw IllegalArgumentException("File not found: $resourcePath")

            val data = Yaml().load<Map<String, Any?>>(inputStream)

            return  extractCustomParameters(data, points)

        }

        private fun calculateDefaultRadius(maxTravelDistance: Double): Double {
            return when {
                maxTravelDistance <= 1.0 -> 0.1
                else -> maxTravelDistance * 0.1  //  10% max travel distance
            }
        }

        private fun extractCustomParameters(data: Map<String, Any?>, points: List<Waypoint>): CustomParameters {

            val result = mutableMapOf<String, Double>()

            val requiredParams = listOf(
                "earthRadiusKm",
                "geofenceCenterLatitude",
                "geofenceCenterLongitude",
                "geofenceRadiusKm"
            )

            for (param in requiredParams) {
                if (!data.containsKey(param)) {
                    throw IllegalArgumentException("Missing param: $param")
                }

                val value = data[param]

                result[param] = when (value) {
                    is Number  -> value.toDouble()
                    else -> throw IllegalArgumentException("$param must be a number")
                }
            }

            if (data.containsKey("mostFrequentedAreaRadiusKm")) {

                val value = data["mostFrequentedAreaRadiusKm"]

                result["mostFrequentedAreaRadiusKm"] = when (value) {
                    is Number  -> value.toDouble()
                    else -> throw IllegalArgumentException("mostFrequentedAreaRadiusKm must be a number")
                }
            } else {
                result["mostFrequentedAreaRadiusKm"] = calculateDefaultRadius(
                    points.maxDistanceFromStart(result["earthRadiusKm"] as Double)?.second?:0.0
                )
            }

            return CustomParameters(
                result["earthRadiusKm"] as Double,
                result["geofenceCenterLatitude"] as Double,
                result["geofenceCenterLongitude"] as Double,
                result["geofenceRadiusKm"] as Double,
                result["mostFrequentedAreaRadiusKm"] as Double
            )
        }
    }
}
