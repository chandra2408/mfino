package io.scal.ambi.model.data.server.responses.newsfeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.BaseResponse
import io.scal.ambi.model.data.server.responses.notebooks.NotebookListItem
import io.scal.ambi.ui.notebooks.list.NotebookData

class NotebooksListResponse : BaseResponse<List<NotebookData>>() {

    @SerializedName("allNotebooks")
    @Expose
    internal var posts1: List<NotebookListItem>? = null


    override fun parse(): List<NotebookData> {
        return posts1.notNullOrThrow("allNotebooks").mapNotNull { it.parse() }
    }
}