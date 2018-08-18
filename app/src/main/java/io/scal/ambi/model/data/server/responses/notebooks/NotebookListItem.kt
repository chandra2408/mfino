package io.scal.ambi.model.data.server.responses.notebooks

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.model.data.server.responses.Parceble
import io.scal.ambi.model.data.server.responses.user.ItemUser
import io.scal.ambi.ui.notebooks.list.NotebookData

class NotebookListItem : Parceble<NotebookData?> {

    @SerializedName("name")
    @Expose
    internal var name: String? = null

    @SerializedName("notes")
    @Expose
    internal var notes: List<NotesItem>? = null

    @SerializedName("files")
    @Expose
    internal var files: List<FilesItem>? = null

    @SerializedName("file")
    @Expose
    internal var file: FilesItem? = null

    @SerializedName("color")
    @Expose
    internal var color: String? = null

    @SerializedName("creator")
    @Expose
    internal var creator: ItemUser? = null

    @SerializedName("permission")
    @Expose
    internal var permission: String? = null

    @SerializedName("isOrganizationContent")
    @Expose
    internal var isOrganizationContent: Boolean? = null

    @SerializedName("deleted")
    @Expose
    internal var deleted: Boolean? = null

    @SerializedName("allMembersAllowed")
    @Expose
    internal var allMembersAllowed: Boolean? = null

    @SerializedName("membership")
    @Expose
    internal var membership: List<MembershipItem>? = null

    @SerializedName("_id")
    @Expose
    internal var _id: String? = null

    private fun getFiles(): List<FilesItem>?{
        var tempList = ArrayList<FilesItem>()
        if(files!=null){
            tempList.addAll(files!!)
        }else if(file!=null){
            tempList.add(file!!)
        }
        return tempList
    }

    override fun parse(): NotebookData? {
        return NotebookData(_id,name,
                notes,
                color,
                creator,
                permission,
                isOrganizationContent,
                deleted,
                allMembersAllowed,
                membership,
                getFiles()?.map { it.parse() }?: emptyList())
    }

}

