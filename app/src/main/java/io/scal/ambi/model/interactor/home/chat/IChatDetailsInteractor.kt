package io.scal.ambi.model.interactor.home.chat

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.base.ServerFile
import io.scal.ambi.entity.chat.ChatMessage
import io.scal.ambi.entity.chat.ChatTypingInfo
import io.scal.ambi.entity.chat.FullChatItem
import io.scal.ambi.entity.user.User
import io.scal.ambi.ui.global.picker.FileResource

interface IChatDetailsInteractor {

    fun loadCurrentUser(): Observable<User>

    fun loadChatInfo(): Observable<FullChatItem>

    fun loadChatPage(lastMessageIndex: Long?): Single<List<ChatMessage>>

    fun loadTypingInfo(): Observable<ChatTypingInfo>

    fun loadSendingMessagesInfo(): Observable<List<ChatMessage>>

    fun loadNewMessages(): Observable<List<ChatMessage>>

    fun sendTextMessage(message: String)

    fun sendPictureMessage(url: String, id: String)

    fun sendOtherMessages(type: String)

    fun sendFileMessage(fileResource: FileResource, mediaType: String)

    fun resendMessage(uid: String)

    fun attachPicture(fileResource: FileResource): Single<ServerFile>
}