//package ripple.trackingmaster.devicetrackapp.data.remote
//
//import retrofit2.Response
//import retrofit2.http.Body
//import retrofit2.http.GET
//import retrofit2.http.POST
//import ripple.trackingmaster.devicetrackapp.data.remote.dto.BeltApiRequest
//import ripple.trackingmaster.devicetrackapp.data.remote.dto.BeltApiResponse
//
//interface BeltApiService {
//
//    @GET("api/belts/belts")
//    suspend fun getBelts(): List<BeltApiResponse>
//
//    @POST("api/belts/add-belt")
//    suspend fun addOrUpdateBelt(@Body beltData: BeltApiRequest): Response<Unit> // Use Response<Unit> for success/fail
//}

package ripple.trackingmaster.devicetrackapp.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ripple.trackingmaster.devicetrackapp.data.remote.dto.BeltApiResponse

interface BeltApiService {

    @GET("belts") // Replace with actual endpoint
    suspend fun getBelts(): List<BeltApiResponse>

    // ✅ NEW: Endpoint to save belt
    @POST("belts") // Replace with actual endpoint (e.g., "device/sync" or "belts")
    suspend fun saveBelt(@Body belt: BeltApiResponse): BeltApiResponse
}