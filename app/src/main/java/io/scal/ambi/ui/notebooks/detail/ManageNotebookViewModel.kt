package io.scal.ambi.ui.notebooks.detail

import android.content.Context
import android.content.Intent
import android.databinding.ObservableField
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.base.ServerFile
import io.scal.ambi.entity.notebooks.FileData
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.data.server.FilesApi
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.global.base.viewmodel.toGoodUserMessage
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.notebooks.create.CreateOrDeleteNotebookDataState
import io.scal.ambi.ui.notebooks.create.CreateOrDeleteNotebookErrorState
import io.scal.ambi.ui.notebooks.create.CreateOrDeleteNotebookState
import io.scal.ambi.ui.notebooks.create.ICreateOrDeleteNotebookInteractor
import io.scal.ambi.ui.notebooks.list.INotebookListViewModel
import io.scal.ambi.ui.notebooks.list.NotebookData
import io.scal.ambi.ui.notebooks.list.NotebookListSearchViewModel
import javax.inject.Inject
import javax.inject.Named


/**
 * Created by chandra on 10-08-2018.
 */
class ManageNotebookViewModel @Inject internal constructor(private val context: Context,
                                                           router: BetterRouter,
                                                           private val interactor: ICreateOrDeleteNotebookInteractor,
                                                           rxSchedulersAbs: RxSchedulersAbs,
                                                           val searchViewModel: NotebookListSearchViewModel,
                                                           @Named("notebookData") val notebookData: NotebookData) :
        BaseUserViewModel(router, { interactor.loadCurrentUser() },rxSchedulersAbs), INotebookListViewModel {

    //1 - MAIN MENU 2 - ADD MENU
    var bottomSheetState = ObservableField<Int>()

    internal val progressState = ObservableField<CreateOrDeleteNotebookState>(CreateOrDeleteNotebookState.TotalProgress)
    internal val errorState = ObservableField<CreateOrDeleteNotebookErrorState>()
    val dataState = ObservableField<CreateOrDeleteNotebookDataState>()


    override fun openNotebookDetails(element: NotebookData) {
        //nothing
    }

    fun back(){
        router.exit()
    }

    fun openActionSheet(actionType: Int){
        bottomSheetState.set(actionType)
    }

    fun deleteNotebook(){
        progressState.set(CreateOrDeleteNotebookState.RefreshProgress)
        interactor.deleteNotebook(notebookData._id!!)
                .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
                .subscribe(
                        {
                            dataState.set(CreateOrDeleteNotebookDataState.CreateOrDeleteNotebookSuccess(dataState.get()?.profileInfo, it.message!!))
                        },
                        {
                            handleError(it, false)
                            errorState.set(CreateOrDeleteNotebookErrorState.NonFatalErrorState(it.toGoodUserMessage(context)))
                            errorState.set(CreateOrDeleteNotebookErrorState.NoErrorState)
                        })
                .addTo(disposables)
    }

    fun doShare(){
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "ambi")
        var sAux = "\nAmbi transforms the student college experience by allowing students to do the following at their fingertips.. \n\n " +
                "1) Connect & Communicate with peers, professors, staff, groups and advisors.\n" +
                "2) Stay up-to-date & Engage with college news, information, services, announcements and events.\n" +
                "3) Collaborate, manage & organize all group work, teams, clubs, classes, projects and personal work.\n\n" +
                "With ambi, students have all the people, information & tools they need at their fingertips. \n\n" +
                "Download now to experience\n\n"
        sAux = sAux + "https://play.google.com/store/apps/details?id=com.ambi.work \n\n"
        i.putExtra(Intent.EXTRA_TEXT, sAux)
        context.startActivity(Intent.createChooser(i, "choose one"))
    }

    fun uploadFile(fileResource: FileResource){
        progressState.set(CreateOrDeleteNotebookState.RefreshProgress)
        interactor.uploadDocument(fileResource).compose(rxSchedulersAbs.getIOToMainTransformerSingle<ServerFile>()).subscribe({
            interactor.attachDocumentToNotebook(notebookData._id!!, FilesApi.FileCreationRequest(it.name, it.url, it.fileType))
                    .compose(rxSchedulersAbs.getIOToMainTransformerSingle<FileData>())
                    .subscribe({
                        dataState.set(CreateOrDeleteNotebookDataState.AttachedFileToNotebookSuccess(null,it))
                    },{ t ->
                        handleError(t)
                        errorState.set(CreateOrDeleteNotebookErrorState.NonFatalErrorState(t.toGoodUserMessage(context)))
                        errorState.set(CreateOrDeleteNotebookErrorState.NoErrorState)
                    }).addTo(disposables);
        },
                { t ->
                    handleError(t)
                    errorState.set(CreateOrDeleteNotebookErrorState.NonFatalErrorState(t.toGoodUserMessage(context)))
                    errorState.set(CreateOrDeleteNotebookErrorState.NoErrorState)
                })
                .addTo(disposables)
    }

    override fun viewFile(element: FileData) {
        if(!element.url.isNullOrEmpty()){
            router.navigateTo(NavigateTo.OPEN_FILE, FileData(element.url, element.fileType))
        }
    }


}