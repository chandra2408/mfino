package io.scal.ambi.ui.notebooks.contact

import android.databinding.ObservableList
import io.scal.ambi.model.data.server.responses.user.UsersDetails

internal sealed class SelectContactState {

    object TotalProgress : SelectContactState()

    object NoProgress : SelectContactState()

    object EmptyProgress : SelectContactState()

    object RefreshProgress : SelectContactState()

    object PageProgress : SelectContactState()
}

internal sealed class SelectContactErrorState {

    object NoErrorState : SelectContactErrorState()

    class FatalErrorState(val error: String) : SelectContactErrorState()

    class NonFatalErrorState(val error: String) : SelectContactErrorState()
}

sealed class SelectContactDataState(open val profileInfo: UsersDetails?) {

    data class DataInfoOnly(override val profileInfo: UsersDetails) : SelectContactDataState(profileInfo)

    data class SelectContactEmpty(override val profileInfo: UsersDetails?) : SelectContactDataState(profileInfo)

    data class SelectContactData(override val profileInfo: UsersDetails?, val newsFeed: List<UsersDetails>) : SelectContactDataState(profileInfo)

    fun copyNotificationInfo(uiProfile: UsersDetails): SelectContactDataState =
        when (this) {
            is DataInfoOnly -> copy(profileInfo = uiProfile)
            is SelectContactEmpty -> copy(profileInfo = uiProfile)
            is SelectContactData -> copy(profileInfo = uiProfile)
        }
}