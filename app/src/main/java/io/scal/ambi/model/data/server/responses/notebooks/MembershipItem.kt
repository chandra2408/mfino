package io.scal.ambi.model.data.server.responses.notebooks

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.responses.user.ItemUser
import java.io.Serializable


/**
 * Created by chandra on 10-08-2018.
 */
class MembershipItem : Serializable{

    @SerializedName("user")
    @Expose
    internal var user: ItemUser? = null

    @SerializedName("_id")
    @Expose
    internal var _id: String? = null

    @SerializedName("type")
    @Expose
    internal var type: Int? = null

    @SerializedName("status")
    @Expose
    internal var status: Int? = null
}