package ripple.trackingmaster.devicetrackapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * The JSON object we send to the POST API
 * /api/belts/add-belt
 */
data class BeltApiRequest(
    val teamMember: String?,
    val macAddress: String,
    val serialNumber: String?,
    val beltSize: String?,
    val assignedTo: String? // This is the site NAME (e.g., "aiims")
)

/**
 * The JSON object we get from the GET API
 * /api/belts/belts
 */
data class BeltApiResponse(
    val id: Int = 0,             // Integer ID
    val macAddress: String,
    val serialNumber: String?,
    val beltSize: String?,       // String (e.g., "34" or "Large")
    val teamMember: String?,
    val assignedTo: String?,
    val createdAt: String = "",
    val updatedAt: String = ""
)