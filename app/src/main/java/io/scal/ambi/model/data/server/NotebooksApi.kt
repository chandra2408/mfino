package io.scal.ambi.model.data.server

import com.google.gson.JsonArray
import io.reactivex.Single
import io.scal.ambi.model.data.server.responses.newsfeed.NotebooksListResponse
import io.scal.ambi.model.data.server.responses.notebooks.NotebookCreationDeletionResponse
import io.scal.ambi.model.data.server.responses.notebooks.NotebookResponse
import io.scal.ambi.ui.home.classes.ClassesData
import io.scal.ambi.ui.home.classes.ClassesResponse
import io.scal.ambi.ui.notebooks.create.GroupDataResponse
import retrofit2.http.*

interface NotebooksApi {

    @GET("v1/classes")
    fun getClasses(): Single<ClassesResponse>

    @GET("v1/classes/filter/all")
    fun getAllClasses(): List<ClassesData>

    @GET("v1/notebooks/all/me")
    fun getNotebookList(): Single<NotebooksListResponse>

    @GET("v1/groups")
    fun getGroups(): Single<GroupDataResponse>

    @POST("v1/notebooks")
    fun createNotebook(@Body body: CreateNotebookRequest): Single<NotebookCreationDeletionResponse>

    @DELETE("v1/notebooks/{notebookId}")
    fun deleteNotebook(@Path("notebookId") query: String): Single<NotebookCreationDeletionResponse>

    @POST("v1/notebooks/{id}/files")
    fun attachFileToNotebook(@Body request: FilesApi.FileCreationRequest, @Path("id") id: String): Single<NotebookResponse>

    @PUT("v1/notebooks/{id}")
    fun updateNotebook(@Body request: UpdateNotebookRequest, @Path("id") notebookId: String): Single<NotebookCreationDeletionResponse>
}

class CreateNotebookRequest(val name: String,
                            val color: String,
                            val creator: String,
                            val owner: String,
                            val notebookHost: String,
                            val notebookHostId: String,
                            val inviteType: String,
                            val membership: JsonArray? = null,
                            val allMembersAllowed: Boolean = true)

class UpdateNotebookRequest(val name: String,
                            val color: String,
                            val creator: String,
                            val owner: JsonArray,
                            val notebookHost: String,
                            val notebookHostId: String)