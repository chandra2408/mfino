package io.scal.ambi.ui.notebooks.list

import android.databinding.ObservableList

internal sealed class NotebookListState {

    object TotalProgress : NotebookListState()

    object NoProgress : NotebookListState()

    object EmptyProgress : NotebookListState()

    object RefreshProgress : NotebookListState()

    object PageProgress : NotebookListState()
}

internal sealed class NotebookListErrorState {

    object NoErrorState : NotebookListErrorState()

    class FatalErrorState(val error: String) : NotebookListErrorState()

    class NonFatalErrorState(val error: String) : NotebookListErrorState()
}

sealed class NotebookListDataState(open val profileInfo: NotebookData?) {

    data class DataInfoOnly(override val profileInfo: NotebookData) : NotebookListDataState(profileInfo)

    data class  NotebookListEmpty(override val profileInfo: NotebookData?) : NotebookListDataState(profileInfo)

    data class  NotebookListData(override val profileInfo: NotebookData?, val newsFeed: ObservableList<NotebookData>) : NotebookListDataState(profileInfo)

    fun copyNotificationInfo(uiProfile: NotebookData): NotebookListDataState =
        when (this) {
            is DataInfoOnly      -> copy(profileInfo = uiProfile)
            is NotebookListEmpty -> copy(profileInfo = uiProfile)
            is NotebookListData -> copy(profileInfo = uiProfile)
        }
}