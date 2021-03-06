package io.scal.ambi.entity.chat

import io.scal.ambi.entity.user.User
import org.joda.time.DateTime
import java.io.Serializable

sealed class ChatMessage(open val uid: String,
                         open val index: Long?,
                         open val sender: User,
                         open val sendDate: DateTime,
                         open val message: String,
                         open val likes: List<User>,
                         open val myMessageState: ChatMyMessageState?) : Serializable {

    data class TextMessage(override val uid: String,
                           override val index: Long?,
                           override val sender: User,
                           override val sendDate: DateTime,
                           override val message: String,
                           override val likes: List<User> = emptyList(),
                           override val myMessageState: ChatMyMessageState? = null, val messageCode: String? = "") :
        ChatMessage(uid, index, sender, sendDate, message, likes, myMessageState)

    data class AttachmentMessage(override val uid: String,
                                 override val index: Long?,
                                 override val sender: User,
                                 override val sendDate: DateTime,
                                 override val message: String,
                                 val attachments: List<ChatAttachment>,
                                 override val likes: List<User> = emptyList(),
                                 override val myMessageState: ChatMyMessageState? = null) :
        ChatMessage(uid, index, sender, sendDate, message, likes, myMessageState) {
        init {
            if (attachments.isEmpty()) {
                throw IllegalArgumentException("attachments can not be empty")
            }
        }
    }

}