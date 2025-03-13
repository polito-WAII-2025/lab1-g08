package it.polito.g08

fun main() {
    val points = Waypoint.fromCSV("/waypoints.csv")
    println(points)

    val parameters = CustomParameters.fromYAML("/custom-parameters.yml")
    println(parameters)
}
