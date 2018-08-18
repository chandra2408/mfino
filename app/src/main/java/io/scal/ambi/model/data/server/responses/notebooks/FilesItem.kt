package io.scal.ambi.model.data.server.responses.notebooks

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.scal.ambi.entity.notebooks.FileData
import io.scal.ambi.model.data.server.responses.Parceble
import java.io.Serializable


open class FilesItem : Parceble<FileData>, Serializable{

    @SerializedName("updatedAt")
    @Expose
    internal var updatedAt: String? = null

    @SerializedName("fileType")
    @Expose
    internal var fileType: String? = null

    @SerializedName("_id")
    @Expose
    internal var _id: String? = null

    @SerializedName("createdAt")
    @Expose
    internal var createdAt: String? = null

    @SerializedName("name")
    @Expose
    internal var name: String? = null

    @SerializedName("__v")
    @Expose
    internal var __v: String? = null

    @SerializedName("deleted")
    @Expose
    internal var deleted: String? = null

    @SerializedName("url")
    @Expose
    internal var url: String? = null

    @SerializedName("size")
    @Expose
    internal var size: String? = null


    private fun getFileTypeExtension(): String{
        var fileFormat = when (fileType) {
            "png" -> "PNG"
            "jpg" -> "JPG"
            "jpeg" -> "JPG"
            "application/pdf" -> "PDF"
            "image/png" -> "PNG"
            "image/jpg" -> "JPG"
            "image/jpeg" -> "JPG"
            "pdf" -> "PDF"
            "PDF" -> "PDF"
            else -> {
                "UKN"
            }
        }
        return fileFormat
    }


    private fun getFileTypeColor(): String{
        var color: String?
        if(fileType!!.toLowerCase() in listOf<String>("png","jpeg","jpg","gif","tif","tiff")){
            color = "#ED1E7A"
        }else if(fileType!!.toLowerCase() in listOf<String>("doc","docx","pages","txt")){
            color = "#02A5F0"
        }else if(fileType!!.toLowerCase() in listOf<String>("ppt","pptx","key")){
            color = "#E9A820"
        }else if(fileType!!.toLowerCase() in listOf<String>("xls","xlsm","xlsx")){
            color = "#22A671"
        }else if(fileType!!.toLowerCase() in listOf<String>("mov","mp4","mp3","flac","aac")){
            color = "#8B51F6"
        }else if(fileType!!.toLowerCase() in listOf<String>("pdf")){
            color = "#D12436"
        }else if(fileType!!.toLowerCase() in listOf<String>("ai","sketch")){
            color = "#FB8903"
        }else if(fileType!!.toLowerCase() in listOf<String>("psd")){
            color = "#ED1E7A"
        }else if(fileType!!.toLowerCase() in listOf<String>("js","html","ps","zip")){
            color = "#8A90A2"
        }else{
            color = "#02A5F0"
        }
        return color
    }


    override fun parse(): FileData {

        return FileData(updatedAt,
                getFileTypeExtension(),
                _id,
                createdAt,
                name,
                __v,
                deleted,
                url,
                size,
                getFileTypeColor())
    }
}

