package io.mustelidae.weasel.utils

import io.mustelidae.weasel.common.Replies
import io.mustelidae.weasel.common.Reply

fun <T> List<T>.toReplies(): Replies<T> = Replies(this)
fun <T> T.toReply(): Reply<T> = Reply(this)
