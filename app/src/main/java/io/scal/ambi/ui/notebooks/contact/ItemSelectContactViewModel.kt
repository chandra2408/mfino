package io.scal.ambi.ui.notebooks.contact

import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.responses.user.UsersDetails

/**
 * Created by chandra on 26-07-2018.
 */

interface ItemSelectContactViewModel{

    fun changeContactSelection(element: UsersDetails)
}