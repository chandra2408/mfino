package io.scal.ambi.model.data.server

import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.FileResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface FilesApi {

    @POST("v1/files")
    fun createFile(@Body request: FileCreationRequest): Single<FileResponse>

    @POST("v1/{endpoint}/{id}/files")
    fun createFile(@Body request: FileCreationRequest, @Path("endpoint") query: String, @Path("id") id: String): Single<FileResponse>

    class FileCreationRequest(val name: String, val url: String, val fileType: String)
}