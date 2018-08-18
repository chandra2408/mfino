package io.scal.ambi.ui.notebooks.create

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.BaseResponse

/**
 * Created by chandra on 11-08-2018.
 */
class GroupDataResponse : BaseResponse<List<GroupData>>(){

    @SerializedName("groups")
    @Expose
    var groups: List<Group>? = null

    override fun parse(): List<GroupData> {
        return groups.notNullOrThrow("groups").mapNotNull { it.parse() }
    }

}