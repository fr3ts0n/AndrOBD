package com.fr3ts0n.ecu.gui.androbd

import android.os.Handler
import android.os.Message

class LastMessages(private val queue: MutableList<Message> = mutableListOf()) {
    fun addMessage(msg: Message) {
        val index = queue.indexOfFirst { it.what == msg.what }
        if (index != -1) queue.removeAt(index)

        queue.add(Message.obtain(msg))
    }

    fun sendAllFromHandler(handler: Handler) = queue.map {
        val copied =  Message.obtain(it)
        handler.sendMessage(it)
        copied
    }

    fun clear() = queue.clear()
}