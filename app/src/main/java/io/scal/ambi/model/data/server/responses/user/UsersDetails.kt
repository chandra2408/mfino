package io.scal.ambi.model.data.server.responses.user

import com.ambi.work.R
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.data.server.responses.ItemPicture
import java.io.Serializable

data class UsersDetails constructor(val _id: String,
                               val name: String,
                               val profilePicture: ItemPicture? = null) : Serializable {

    val modifiedName: String = name.trim()
    var isContactSelected: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        return if (other is User) _id == other.uid else false
    }

    override fun hashCode(): Int {
        return if(_id==null) 0 else _id?.hashCode()
    }

    open fun parseAvatar(): IconImageUser {
        return profilePicture?.parse()?.let { IconImageUser(it.iconPath) } ?: IconImageUser(R.drawable.ic_profile.toFrescoImagePath())
    }
}