package org.d3if0037.catatanku.model

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if0037.catatanku.network.ApiStatus
import org.d3if0037.catatanku.network.CatatanApi
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<Note>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

//    init {
//        retrieveData()
//    }

    fun retrieveData(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                val result = CatatanApi.service.getCatatan(userId)
                Log.d("MainViewModel", "Succsess: $result")

                data.value = CatatanApi.service.getCatatan(userId)
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failur: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun saveData(
        userId: String,
        judul: String,
        deskripsi: String,
        bitmap: Bitmap
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = CatatanApi.service.postCatatan(
                    userId,
                    judul.toRequestBody("text/plain".toMediaTypeOrNull()),
                    deskripsi.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )

                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure2: ${e.message}")
                errorMessage.value = "${e.message}"
            }
        }
    }

    fun deleteData(userId: String, hewanId: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = CatatanApi.service.deleteCatatan(userId, hewanId)
                if (response.status == "success") {
                    Log.d("MainViewModel", "Image deleted successfully: $hewanId")
                    retrieveData(userId) // Refresh data after deletion
                } else {
                    Log.d("MainViewModel", "Failed to delete the image: ${response.message}")
                    errorMessage.value = "Failed to delete the image: ${response.message}"
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }


    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
        return MultipartBody.Part.createFormData(
            "imageId", "image.jpg", requestBody)
    }

    fun clearMessage() { errorMessage.value = null }
}