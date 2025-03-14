package it.polito.g08

fun main() {
    val points = Waypoint.fromCSV("/waypoints.csv")
    println(points)

    val maxTravelDistance = 0.5
    val parameters = CustomParameters.fromYAML("/custom-parameters.yml", maxTravelDistance)
    println(parameters)

}
