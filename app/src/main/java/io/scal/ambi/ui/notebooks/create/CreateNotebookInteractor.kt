package io.scal.ambi.ui.notebooks.create

import com.google.gson.JsonArray
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.base.ServerFile
import io.scal.ambi.entity.notebooks.FileData
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.FilesApi
import io.scal.ambi.model.data.server.responses.notebooks.NotebookCreationDeletionResponse
import io.scal.ambi.model.interactor.base.IFileUploadInteractor
import io.scal.ambi.model.repository.data.newsfeed.INotebooksRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.home.classes.ClassesData
import io.scal.ambi.ui.notebooks.list.NotebookData
import javax.inject.Inject

class CreateNotebookInteractor @Inject constructor(private val postsRepository: INotebooksRepository,
                                                   private val fileUploadInteractor: IFileUploadInteractor,
                                                   private val localUserDataRepository: ILocalUserDataRepository) : ICreateOrDeleteNotebookInteractor {

    override fun updateNotebook(name: String, color: String, creator: String, owner: JsonArray, notebookHost: String, notebookHostId: String): Single<NotebookCreationDeletionResponse> {
        return postsRepository.updateNotebook(name,color,creator,owner,notebookHost,notebookHostId)
    }

    override fun attachDocumentToNotebook(id: String, request: FilesApi.FileCreationRequest): Single<FileData> {
        return postsRepository.attachDocumentToNotebook(id,request)
    }

    override fun uploadDocument(fileResource: FileResource): Single<ServerFile> {
        return fileUploadInteractor.uploadDocument(fileResource)
    }


    private var currentUser: User? = null

    override fun uploadFile(fileResource: FileResource): Single<ServerFile> {
        return fileUploadInteractor.uploadImage(fileResource, currentUser!!)
    }

    override fun getAllClasses(): Single<List<ClassesData?>> {
        return postsRepository.getAllClasses()
    }

    override fun deleteNotebook(notebookId: String): Single<NotebookCreationDeletionResponse> {
        return postsRepository.deleteNotebook(notebookId)
    }

    override fun createNotebook(name: String, color: String, creator: String, owner: String, notebookHost: String, notebookHostId: String,inviteType: String,
                                membership: JsonArray?): Single<NotebookCreationDeletionResponse> {
        return postsRepository.ceateNotebook(name, color, creator, owner, notebookHost, notebookHostId,inviteType,membership)
    }

    override fun loadCurrentUser(): Observable<User> =
            localUserDataRepository.observeCurrentUser().doOnNext { currentUser = it }

    override fun loadClasses(page: Int): Single<List<ClassesData>> {
        return postsRepository.loadClasses(page.toLong())
    }

    override fun loadGroups(): Single<List<GroupData>> {
        return postsRepository.loadGroups()
    }

    override fun loadNotebooks(): Single<List<NotebookData>> {
        return postsRepository.loadNotebooks()
    }

}