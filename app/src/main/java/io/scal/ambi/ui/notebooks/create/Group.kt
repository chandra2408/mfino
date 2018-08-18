package io.scal.ambi.ui.notebooks.create

/**
 * Created by chandra on 11-08-2018.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.model.data.server.responses.Parceble

class Group : Parceble<GroupData?> {

    override fun parse(): GroupData? {
        return GroupData(this)
    }

    @SerializedName("_id")
    @Expose
    var id: String? = null
    @SerializedName("slug")
    @Expose
    var slug: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("bannerPicture")
    @Expose
    var bannerPicture: String? = null
    @SerializedName("groupType")
    @Expose
    var groupType: String? = null
    @SerializedName("creator")
    @Expose
    var creator: String? = null
    @SerializedName("channelSids")
    @Expose
    var channelSids: List<String>? = null
    @SerializedName("__v")
    @Expose
    var v: Int? = null
    @SerializedName("twilioSid")
    @Expose
    var twilioSid: String? = null
    @SerializedName("kind")
    @Expose
    var kind: String? = null
    @SerializedName("deleted")
    @Expose
    var deleted: Boolean? = null
    @SerializedName("folders")
    @Expose
    var folders: List<Any>? = null
    @SerializedName("notebooks")
    @Expose
    var notebooks: List<String>? = null
    @SerializedName("color")
    @Expose
    var color: String? = null
    @SerializedName("requestedToJoin")
    @Expose
    var requestedToJoin: List<String>? = null
    @SerializedName("invited")
    @Expose
    var invited: List<Any>? = null
    @SerializedName("members")
    @Expose
    var members: List<String>? = null
    @SerializedName("admins")
    @Expose
    var admins: List<Any>? = null
    @SerializedName("owners")
    @Expose
    var owners: List<String>? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null
    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null
    @SerializedName("isPrivate")
    @Expose
    var isPrivate: Boolean? = null

}