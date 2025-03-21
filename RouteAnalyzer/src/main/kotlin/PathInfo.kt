package it.polito.g08

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.descriptors.*

@Serializable(with = PathInfoSerializer::class)
data class PathInfo(
    val waypoint: Waypoint,
    val areaOrDistance: Double,
    val count: Int? = null,
    val waypoints: List<Waypoint>? = null,
    val firstParamName: String,
    val secondParamName: String,
    val thirdParamName: String? = null,
    val fourthParamName: String? = null
)



object PathInfoSerializer : KSerializer<PathInfo> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PathInfo")

    override fun serialize(encoder: Encoder, value: PathInfo) {
        val jsonEncoder = encoder as? JsonEncoder ?: error("Only JSON serialization is supported")
        val jsonObject = buildJsonObject {
            put(value.firstParamName, Json.encodeToJsonElement(value.waypoint))
            put(value.secondParamName, Json.encodeToJsonElement(value.areaOrDistance))

            value.thirdParamName?.let { key ->
                value.count?.let { put(key, Json.encodeToJsonElement(it)) }
            }

            value.fourthParamName?.let { key ->
                value.waypoints?.let { put(key, Json.encodeToJsonElement(it)) }
            }
        }
        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): PathInfo {
        error("Deserialization is not supported with dynamic field names")
    }
}
