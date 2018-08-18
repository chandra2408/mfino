package io.scal.ambi.ui.home.chat.details.data

import com.ambi.work.BuildConfig
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.extensions.view.IconImage
import org.joda.time.DateTime

sealed class UIChatMessage(open val uid: String,
                           open val index: Long?,
                           open val sender: User,
                           open val state: UIChatMessageStatus,
                           open val messageDateTime: DateTime,
                           open val likes: UIChatLikes,
                           var alignLeft: Boolean? = false) {

    data class TextMessage(override val uid: String,
                           override val index: Long?,
                           override val sender: User,
                           override val state: UIChatMessageStatus,
                           val message: String,
                           override val messageDateTime: DateTime,
                           override val likes: UIChatLikes) : UIChatMessage(uid, index, sender, state, messageDateTime, likes)

    data class ImageMessage(override val uid: String,
                            override val index: Long?,
                            override val sender: User,
                            override val state: UIChatMessageStatus,
                            val message: String,
                            val image: IconImage,
                            override val messageDateTime: DateTime,
                            override val likes: UIChatLikes) : UIChatMessage(uid, index, sender, state, messageDateTime, likes){

        var isResource = image.iconPath.startsWith("res")

    }

    data class AttachmentMessage(override val uid: String,
                                 override val index: Long?,
                                 override val sender: User,
                                 override val state: UIChatMessageStatus,
                                 val attachment: ChatAttachment.File,
                                 val message: String,
                                 val description: String,
                                 override val messageDateTime: DateTime,
                                 override val likes: UIChatLikes) : UIChatMessage(uid, index, sender, state, messageDateTime, likes)

    data class URLMessage(override val uid: String,
                          override val index: Long?,
                          override val sender: User,
                          override val state: UIChatMessageStatus,
                          val attachment: String,
                          val message: String,
                          val description: String,
                          override val messageDateTime: DateTime,
                          override val likes: UIChatLikes) : UIChatMessage(uid, index, sender, state, messageDateTime, likes){

        fun parse(): IconImage {
            return attachment.notNullOrThrow("url")
                    .let { if (it.startsWith("http")) it else BuildConfig.MAIN_IMAGES_URL + it }
                    .let { IconImage(it) }
        }

        var icon = parse()
    }


}