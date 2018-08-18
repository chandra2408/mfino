package io.scal.ambi.model.repository.data.newsfeed

import com.google.gson.JsonArray
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import io.scal.ambi.entity.notebooks.FileData
import io.scal.ambi.model.data.server.CreateNotebookRequest
import io.scal.ambi.model.data.server.FilesApi
import io.scal.ambi.model.data.server.NotebooksApi
import io.scal.ambi.model.data.server.UpdateNotebookRequest
import io.scal.ambi.model.data.server.responses.notebooks.NotebookCreationDeletionResponse
import io.scal.ambi.model.repository.local.LocalUserDataRepository
import io.scal.ambi.ui.home.classes.ClassesData
import io.scal.ambi.ui.notebooks.create.GroupData
import io.scal.ambi.ui.notebooks.list.NotebookData
import javax.inject.Inject

class NotebooksRepository @Inject constructor(private val postsApi: NotebooksApi,
                                              private val localUserDataRepository: LocalUserDataRepository) : INotebooksRepository {


    override fun attachDocumentToNotebook(notebookId: String, request: FilesApi.FileCreationRequest): Single<FileData> {
        return postsApi.attachFileToNotebook(request,notebookId).map { it.parse() }
    }

    override fun getAllClasses(): Single<List<ClassesData?>> {
        return postsApi.getAllClasses().toSingle()
    }

    override fun deleteNotebook(notebookId: String): Single<NotebookCreationDeletionResponse> {
        return postsApi.deleteNotebook(notebookId)
    }

    override fun loadGroups(): Single<List<GroupData>> {
        return postsApi.getGroups()
                .map { it.parse() }
    }

    override fun loadClasses(page: Long): Single<List<ClassesData>> {
        return postsApi.getClasses()
                .map { it.parse() }
    }

    override fun loadNotebooks(): Single<List<NotebookData>> {
        return postsApi.getNotebookList()
                .map { it.parse() }
    }

    override fun ceateNotebook(name: String,
                               color: String,
                               creator: String,
                               owner: String,
                               notebookHost: String,
                               notebookHostId: String,
                               inviteType: String,
                               membership: JsonArray?): Single<NotebookCreationDeletionResponse> {
        return postsApi.createNotebook(CreateNotebookRequest(name,
                color,
                creator,
                owner,
                notebookHost,
                notebookHostId,
                inviteType,
                membership))
    }

    override fun updateNotebook(name: String,
                                color: String,
                                creator: String,
                                owner: JsonArray,
                                notebookHost: String,
                                notebookHostId: String): Single<NotebookCreationDeletionResponse> {
        return postsApi.updateNotebook(UpdateNotebookRequest(name,
                color,
                creator,
                owner,
                notebookHost,
                notebookHostId),notebookHostId)
    }
}