package io.scal.ambi.ui.notebooks.create

import io.scal.ambi.entity.notebooks.FileData
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.ui.home.classes.ClassesData

internal sealed class CreateOrDeleteNotebookState {

    object TotalProgress : CreateOrDeleteNotebookState()

    object NoProgress : CreateOrDeleteNotebookState()

    object EmptyProgress : CreateOrDeleteNotebookState()

    object RefreshProgress : CreateOrDeleteNotebookState()

    object PageProgress : CreateOrDeleteNotebookState()
}

internal sealed class CreateOrDeleteNotebookErrorState {

    object NoErrorState : CreateOrDeleteNotebookErrorState()

    class FatalErrorState(val error: String) : CreateOrDeleteNotebookErrorState()

    class NonFatalErrorState(val error: String) : CreateOrDeleteNotebookErrorState()
}

sealed class CreateOrDeleteNotebookDataState(open val profileInfo: Any?) {


    data class DataInfoOnly(override val profileInfo: Any) : CreateOrDeleteNotebookDataState(profileInfo)

    data class  CreateNotebookDataEmpty(override val profileInfo: Any?) : CreateOrDeleteNotebookDataState(profileInfo)

    data class  ClassesDataInfo(override val profileInfo: Any?, val newsFeed: List<ClassesData>) : CreateOrDeleteNotebookDataState(profileInfo)

    data class  GroupInfo(override val profileInfo: Any?, val newsFeed: List<GroupData>) : CreateOrDeleteNotebookDataState(profileInfo)

    data class CreateOrDeleteNotebookSuccess(override val profileInfo: Any?, val message: String) : CreateOrDeleteNotebookDataState(profileInfo)

    data class AttachedFileToNotebookSuccess(override val profileInfo: Any?, val fileData: FileData) : CreateOrDeleteNotebookDataState(profileInfo)

    fun copyNotificationInfo(uiProfile: Any): CreateOrDeleteNotebookDataState =
        when (this) {
            is DataInfoOnly      -> copy(profileInfo = uiProfile)
            is CreateNotebookDataEmpty -> copy(profileInfo = uiProfile)
            is ClassesDataInfo -> copy(profileInfo = uiProfile)
            is GroupInfo -> copy(profileInfo = uiProfile)
            is CreateOrDeleteNotebookSuccess -> copy(profileInfo = uiProfile)
            is AttachedFileToNotebookSuccess -> copy(profileInfo = uiProfile)
        }
}

sealed class CreateNotebookStateModel {
    open val notebookTitle: ObservableString? = null
    open val notebookColor: ObservableString? = null
    open val errorMessage: String? = null
    open val progress = false

    internal open class DataInputStateModel(notebookTitle: String?,
                                            notebookColor: String?) : CreateNotebookStateModel() {
        override val notebookTitle = ObservableString(notebookTitle)
        override val notebookColor = ObservableString(notebookColor)
    }

    internal class DataInputErrorStateModel(override val errorMessage: String,userName: String?,password: String?) : DataInputStateModel(userName, password)

    internal class ProgressStateModel(email: String?,password: String?) : CreateNotebookStateModel() {
        override val notebookTitle = ObservableString(email)
        override val notebookColor = ObservableString(password)
        override val progress = true
    }

}