import com.uber.h3core.util.LatLng
import it.polito.g08.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

const val EARTH_RADIUS = 6372.0

val testWaypoints = listOf(
    Waypoint(1742653227757.0,45.06363,7.65908),
    Waypoint(1742653227773.503,45.06281,7.65854),
    Waypoint(1742653227790.0059,45.06201,7.65797),
    Waypoint(1742653227806.5088,45.06189,7.65712),
    Waypoint(1742653227823.0117,45.06230,7.65603),
    Waypoint(1742653227839.5146,45.06178,7.65697),
    Waypoint(1742653227856.0176,45.06134,7.65809),
    Waypoint(1742653227872.5205,45.06091,7.65921),
    Waypoint(1742653227889.0234,45.06048,7.66032),
    Waypoint(1742653227905.5264,45.06038,7.66130),
    Waypoint(1742653227922.0293,45.06118,7.66187),
    Waypoint(1742653227938.5322,45.06199,7.66244),
    Waypoint(1742653227955.0352,45.06279,7.66301),
    Waypoint(1742653227971.538,45.06359,7.66359),
    Waypoint(1742653227988.041,45.06440,7.66416)
)
val testWaypointMultiplestops = listOf(
    Waypoint(1742653227757.0,45.06363,7.65908),
    Waypoint(1742653227773.503,45.06281,7.65854),
    Waypoint(1742653227790.0059,45.06201,7.65797),
    Waypoint(1742653227790.0059,45.06201,7.65797),
    Waypoint(1742653227806.5088,45.06189,7.65712),
    Waypoint(1742653227823.0117,45.06230,7.65603),
    Waypoint(1742653227839.5146,45.06178,7.65697),
    Waypoint(1742653227856.0176,45.06134,7.65809),
    Waypoint(1742653227872.5205,45.06091,7.65921),
    Waypoint(1742653227872.5205,45.06091,7.65921),
    Waypoint(1742653227889.0234,45.06048,7.66032),
    Waypoint(1742653227905.5264,45.06038,7.66130),
    Waypoint(1742653227922.0293,45.06118,7.66187),
    Waypoint(1742653227938.5322,45.06199,7.66244),
    Waypoint(1742653227955.0352,45.06279,7.66301),
    Waypoint(1742653227971.538,45.06359,7.66359),
    Waypoint(1742653227988.041,45.06440,7.66416)
)

class WaypointsTests {
    @Test
    fun distanceMaxDistanceFromStartEmptyList() {
        val list: List<Waypoint> = listOf()
        val res = list.maxDistanceFromStart(EARTH_RADIUS)
        assertEquals(null, res)
    }

    @Test
    fun distanceMaxDistanceFromStartSingleElement() {
        val list: List<Waypoint> = testWaypoints.subList(0, 1)
        val res = list.maxDistanceFromStart(EARTH_RADIUS)
        assertEquals(null, res)
    }

    @Test
    fun distanceMaxDistanceFromStartNegativeRadius() {
        val res = testWaypoints.maxDistanceFromStart(-EARTH_RADIUS)
        assertEquals(null, res)
    }

    @Test
    fun distanceMaxDistanceFromStart() {
        val expected = Pair(Waypoint(timestamp=1.742653227988041E12, latitude=45.0644, longitude=7.66416, cell=0), 0.408124694734578)
        val res = testWaypoints.maxDistanceFromStart(EARTH_RADIUS)
        assertEquals(expected, res)
    }

    @Test
    fun waypointsOutsideGeofenceEmptyList() {
        val geofenceCenter = testWaypoints.first().let {
            LatLng(it.latitude, it.longitude)
        }
        val res = listOf<Waypoint>().waypointsOutsideGeofence(geofenceCenter, 1.0, EARTH_RADIUS)
        assertEquals(listOf<Waypoint>(), res)
    }

    @Test
    fun waypointsOutsideGeofenceAllPointsInside() {
        val geofenceCenter = testWaypoints.first().let {
            LatLng(it.latitude, it.longitude)
        }
        val res = testWaypoints.subList(0, 3).waypointsOutsideGeofence(geofenceCenter, 1.0, EARTH_RADIUS)
        assertEquals(listOf<Waypoint>(), res)
    }

    @Test
    fun waypointsOutsideGeofence() {
        val expected = listOf(
            Waypoint(1.742653227773503E12, 45.06281, 7.65854, 0),
            Waypoint(1.7426532277900059E12, 45.06201, 7.65797, 0)
        )
        val geofenceCenter = testWaypoints.first().let {
            LatLng(it.latitude, it.longitude)
        }
        val res = testWaypoints.subList(0, 3).waypointsOutsideGeofence(geofenceCenter, 0.1, EARTH_RADIUS)
        assertEquals(expected, res)
    }

    @Test
    fun mostFrequentedAreaEmptyList() {
        val res = listOf<Waypoint>().mostFrequentedArea(EARTH_RADIUS)
        assertEquals(null, res)
    }

    @Test
    fun mostFrequentedArea() {
        val expected = Pair(Waypoint(0.0, 79.24239850975904, 38.02340700796989, 0), 15)
        val res = testWaypoints.mostFrequentedArea(EARTH_RADIUS)
        assertEquals(expected, res)
    }

    @Test
    fun distanceTravelledByAreaOnlyOneElement() {
        val res = testWaypoints.subList(0, 1).distanceTravelledByArea(EARTH_RADIUS)
        assertEquals(0.0, res)
    }

    @Test
    fun distanceTravelledByAreaEmptyList() {
        val res = listOf<Waypoint>().distanceTravelledByArea(EARTH_RADIUS)
        assertEquals(0.0, res)
    }

    @Test
    fun distanceTravelledByArea() {
        val res = testWaypoints.distanceTravelledByArea(EARTH_RADIUS)
        assertEquals(1.3374666936896162, res)
    }

    @Test
    fun distanceTravelledByDistancePoints() {
        val res = testWaypoints.distanceTravelledByDistancePoints(EARTH_RADIUS)
        assertEquals(1.4005957080539282, res)
    }

    @Test
    fun distanceTravelledByDistancePointsEmptyList() {
        val res = testWaypoints.subList(0, 1).distanceTravelledByDistancePoints(EARTH_RADIUS)
        assertEquals(0.0, res)
    }

    @Test
    fun distanceTravelledByDistancePointsOnlyOneElement() {
        val res = listOf<Waypoint>().distanceTravelledByDistancePoints(EARTH_RADIUS)
        assertEquals(0.0, res)
    }

    @Test
    fun getStopsOnlyFirstAndLast() {
        val res = testWaypoints.getStops()
        assertEquals(listOf(testWaypoints.first(), testWaypoints.last()), res)
    }

    @Test
    fun getStopsWithIntermediateStops() {
        val res = testWaypointMultiplestops.getStops()
        assertEquals(listOf(testWaypointMultiplestops.first(), testWaypointMultiplestops[3], testWaypointMultiplestops[9], testWaypointMultiplestops.last()), res)
    }
}