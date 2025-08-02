package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    private var currentDelay = DELAY_DEFAULT

    fun getReplyMessage(): Flow<String> {
        return api.getReply()
            .onEach { currentDelay = DELAY_DEFAULT }
            .retryWhen { _, _ ->
                delay(currentDelay)
                currentDelay *= DELAY_FACTOR
                return@retryWhen true
            }
    }

    companion object {
        private const val DELAY_FACTOR = 2
        private const val DELAY_DEFAULT = 100L
    }
}