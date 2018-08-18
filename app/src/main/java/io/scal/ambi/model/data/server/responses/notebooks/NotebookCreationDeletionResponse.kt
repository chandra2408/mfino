package io.scal.ambi.model.data.server.responses.notebooks

import io.scal.ambi.model.data.server.responses.BaseResponse

/**
 * Created by chandra on 11-08-2018.
 */
class NotebookCreationDeletionResponse : BaseResponse<String>(){

    override fun parse(): String {
        return message!!
    }

}
