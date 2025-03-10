package it.polito.g08

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV

@DataSchema
data class Waypoint(
    val timestamp: Double,
    val latitude: Double,
    val longitude: Double,
) {

    companion object {
        fun fromCSV(resourcePath: String): List<Waypoint> {
            val inputStream = object {}.javaClass.getResourceAsStream(resourcePath)
                ?: throw IllegalArgumentException("File not found: $resourcePath")
            return DataFrame.readCSV(
                inputStream,
                header = listOf("timestamp", "latitude", "longitude"),
                delimiter = ';'

            ).cast<Waypoint>().toList()
        }
    }
}
