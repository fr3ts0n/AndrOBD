package com.fr3ts0n.ecu.gui.androbd

import android.os.Handler
import android.os.Message

class LastMessages(private val queue: MutableList<Message> = mutableListOf()) {
    fun addMessage(msg: Message) {
        val index = queue.indexOfFirst { it.what == msg.what }
        if (index != -1) queue.removeAt(index)

        queue.add(Message.obtain(msg))
    }

    fun sendAllFromHandler(handler: Handler) = queue.forEach { handler.sendMessage(it) }

    fun clear() = queue.clear()
}