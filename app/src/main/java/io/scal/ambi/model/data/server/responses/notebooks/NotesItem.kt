package io.scal.ambi.model.data.server.responses.notebooks
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.model.data.server.responses.user.ItemUser
import org.joda.time.DateTime
import java.io.Serializable

data class NotesItem(val _id: String? = null,
                     val creator: ItemUser? = null,
                     val name: String? = null,
                     val content: String? = null,
                     val deleted: Boolean? = null,
                     val createdAt: Long? = null,
                     var updatedAt: String? = null) : Serializable {

    var formattedUpdatedTime = if(updatedAt!=null) updatedAt.toDateTime("updatedAt") else DateTime.now()

    private fun String?.toDateTime(fieldName: String): DateTime {
        val notNullString = notNullOrThrow(fieldName)
        return DateTime.parse(notNullString)
    }



}