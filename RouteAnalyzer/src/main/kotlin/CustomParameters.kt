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

        fun fromYAML(resourcePath: String, maxTravelDistance: Double): CustomParameters {

            val inputStream: InputStream = object {}.javaClass.getResourceAsStream(resourcePath)
                ?: throw IllegalArgumentException("File not found: $resourcePath")

            val data = Yaml().load<Map<String, Any?>>(inputStream)
            val validatedData = validateAndCompleteCustomParameters(data, maxTravelDistance)


            return CustomParameters(
                validatedData["earthRadiusKm"] as Double,
                validatedData["geofenceCenterLatitude"] as Double,
                validatedData["geofenceCenterLongitude"] as Double,
                validatedData["geofenceRadiusKm"] as Double,
                validatedData["mostFrequentedAreaRadiusKm"] as Double
            )
        }

        private fun calculateDefaultRadius(maxTravelDistance: Double): Double {
            return when {
                maxTravelDistance <= 1.0 -> 0.1
                else -> maxTravelDistance * 0.1  //  10% max travel distance
            }
        }

        private fun validateAndCompleteCustomParameters(data: Map<String, Any?>, maxTravelDistance: Double): Map<String, Double> {

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
                if (value !is Double && value !is Int && value !is Float) {
                    throw IllegalArgumentException("$param must be a number, found: ${value?.javaClass?.simpleName}")
                }

                result[param] = when (value) {
                    is Double -> value
                    is Int -> value.toDouble()
                    is Float -> value.toDouble()
                    else -> throw IllegalArgumentException("$param must be a number")
                }
            }

            if (data.containsKey("mostFrequentedAreaRadiusKm")) {
                val value = data["mostFrequentedAreaRadiusKm"]
                if (value !is Double && value !is Int && value !is Float) {
                    throw IllegalArgumentException("mostFrequentedAreaRadiusKm must be a number, found: ${value?.javaClass?.simpleName}")
                }

                result["mostFrequentedAreaRadiusKm"] = when (value) {
                    is Double -> value
                    is Int -> value.toDouble()
                    is Float -> value.toDouble()
                    else -> throw IllegalArgumentException("mostFrequentedAreaRadiusKm must be a number")
                }
            } else {

                result["mostFrequentedAreaRadiusKm"] = calculateDefaultRadius(maxTravelDistance)
            }

            return result
        }
    }
}