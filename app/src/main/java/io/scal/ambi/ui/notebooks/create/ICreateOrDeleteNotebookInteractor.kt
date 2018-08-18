package io.scal.ambi.ui.notebooks.create

import com.google.gson.JsonArray
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.base.ServerFile
import io.scal.ambi.entity.notebooks.FileData
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.FilesApi
import io.scal.ambi.model.data.server.responses.notebooks.NotebookCreationDeletionResponse
import io.scal.ambi.model.data.server.responses.notebooks.NotebookListItem
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.home.classes.IClassesInteractor
import io.scal.ambi.ui.notebooks.list.NotebookData
import org.json.JSONArray

/**
 * Created by chandra on 10-08-2018.
 */
interface ICreateOrDeleteNotebookInteractor : IClassesInteractor{

    fun loadNotebooks(): Single<List<NotebookData>>

    fun loadGroups(): Single<List<GroupData>>

    fun createNotebook(name: String,
                       color: String,
                       creator: String,
                       owner: String,
                       notebookHost: String,
                       notebookHostId: String,
                       inviteType: String,
                       membership: JsonArray?): Single<NotebookCreationDeletionResponse>

    fun deleteNotebook(notebookId: String): Single<NotebookCreationDeletionResponse>

    fun uploadFile(fileResource: FileResource): Single<ServerFile>

    fun uploadDocument(fileResource: FileResource): Single<ServerFile>

    fun attachDocumentToNotebook(id: String,request: FilesApi.FileCreationRequest): Single<FileData>

    fun updateNotebook(name: String,
                       color: String,
                       creator: String,
                       owner: JsonArray,
                       notebookHost: String,
                       notebookHostId: String) : Single<NotebookCreationDeletionResponse>
}