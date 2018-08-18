package io.scal.ambi.ui.notebooks.list

import com.ambi.work.R
import io.scal.ambi.entity.notebooks.FileData
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.data.server.responses.notebooks.MembershipItem
import io.scal.ambi.model.data.server.responses.notebooks.NotesItem
import io.scal.ambi.model.data.server.responses.user.ItemUser
import java.io.Serializable

/**
 * Created by chandra on 10-08-2018.
 */

data class NotebookData(val _id: String?, val title: String?,
                        val notes: List<NotesItem>? = emptyList(),
                        val color: String?,
                        val creator: ItemUser?,
                        val permission: String?=null,
                        val isOrganizationContent: Boolean? = false,
                        val deleted: Boolean? = false,
                        val allMembersAllowed: Boolean? = false,
                        val membership: List<MembershipItem>? = emptyList(),
                        val files: List<FileData>? = emptyList()): Serializable {


    var avatar = parseAvatar()
    var isOnlyYou = false
    var memberCount = 0;

    fun parseAvatar(): IconImageUser {
        return creator?.profilePicture?.parse()?.let { IconImageUser(it.iconPath) } ?: IconImageUser(R.drawable.ic_profile.toFrescoImagePath())
    }

    fun isOnlyYou(userId: String?): Boolean{
        return (membership?.size == 1 && membership.get(0).user?._id.equals(userId))
    }

    fun memberCount(userId: String?): Int{
        var hasUser = false
        for(s:MembershipItem in this?.membership!!){

            if(s.user?.id.equals(userId)){
                hasUser = true
                break
            }
        }
        return (if(hasUser) this?.membership!!.size-1 else this?.membership!!.size)
    }

}