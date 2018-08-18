package io.scal.ambi.model.data.server.responses.notebooks

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.notebooks.FileData
import io.scal.ambi.model.data.server.responses.BaseResponse

class NotebookResponse : BaseResponse<FileData?>() {

    @SerializedName("file")
    @Expose
    internal var file: FilesItem? = null


    override fun parse(): FileData? {
        var fileData = file?.parse()
        fileData?.message = message
        return fileData
    }


}

