package io.scal.ambi.model.repository.data.newsfeed

import com.google.gson.JsonArray
import io.reactivex.Single
import io.scal.ambi.entity.notebooks.FileData
import io.scal.ambi.model.data.server.FilesApi
import io.scal.ambi.model.data.server.responses.notebooks.NotebookCreationDeletionResponse
import io.scal.ambi.ui.home.classes.ClassesData
import io.scal.ambi.ui.notebooks.create.GroupData
import io.scal.ambi.ui.notebooks.list.NotebookData
import org.json.JSONArray

interface INotebooksRepository {

    fun loadNotebooks(): Single<List<NotebookData>>

    fun loadClasses(page: Long): Single<List<ClassesData>>

    fun getAllClasses(): Single<List<ClassesData?>>

    fun loadGroups(): Single<List<GroupData>>

    fun ceateNotebook(name: String,
                      color: String,
                      creator: String,
                      owner: String,
                      notebookHost: String,
                      notebookHostId: String,
                      inviteType: String,
                      membership: JsonArray?): Single<NotebookCreationDeletionResponse>

    fun deleteNotebook(notebookId: String) : Single<NotebookCreationDeletionResponse>

    fun attachDocumentToNotebook(notebookId: String, request: FilesApi.FileCreationRequest): Single<FileData>

    fun updateNotebook(name: String,
                       color: String,
                       creator: String,
                       owner: JsonArray,
                       notebookHost: String,
                       notebookHostId: String): Single<NotebookCreationDeletionResponse>

}