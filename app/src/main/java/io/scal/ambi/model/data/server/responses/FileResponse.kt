package io.scal.ambi.model.data.server.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.base.ServerFile
import io.scal.ambi.extensions.notNullOrThrow
import java.io.File

class FileResponse : BaseResponse<ServerFile>() {

    @SerializedName("file")
    @Expose
    var file: ItemFile? = null

    override fun parse(): ServerFile {
        return file.notNullOrThrow("file").parse()
    }

    class ItemFile : Parceble<ServerFile> {

        @SerializedName("_id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("url")
        @Expose
        var url: String? = null

        @SerializedName("fileType")
        @Expose
        var fileType: String? = null

        @SerializedName("fileSize")
        @Expose
        var fileSize: Long? = null

        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = null

        private fun getFileName(): String{
            if(name==null){
                return ""
            }else{
                return name!!
            }
        }

        override fun parse(): ServerFile {
            return ServerFile(getFileName(),id.notNullOrThrow("id"),
                              url.notNullOrThrow("url"),
                              fileType ?: extractFromUrl(url.notNullOrThrow("url")),
                              fileSize ?: 0
            )
        }

        private fun extractFromUrl(url: String): String = File(url).extension
    }
}