package org.d3if0037.catatanku.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.d3if0037.catatanku.model.Note
import org.d3if0037.catatanku.model.OpStatus
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val BASE_URL = "https://c3e6-114-79-49-192.ngrok-free.app/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
    .baseUrl(BASE_URL)
    .build()

interface TempatApiService {
    @GET("belajarRestApiWeb/files/adrian/view.php")
    suspend fun getCatatan(): List<Note>

    @Multipart
    @POST("belajarRestApiWeb/files/adrian/AddCatatan.php")
    suspend fun postCatatan(
        @Header("Authorization") userId: String,
        @Part("judul") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part imageId: MultipartBody.Part
    ): OpStatus
}

object CatatanApi {
    val service: TempatApiService by lazy {
        retrofit.create(TempatApiService::class.java)
    }

    fun getCatatanUrl(imageId: String): String {
        val encodedImageId = imageId.replace("&", "%26")
        return "${BASE_URL}belajarRestApiWeb/files/adrian/image.php?imageId=$encodedImageId"
    }

}

enum class ApiStatus { LOADING, SUCCESS, FAILED }