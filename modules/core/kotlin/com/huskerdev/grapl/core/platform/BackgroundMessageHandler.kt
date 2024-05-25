package com.huskerdev.grapl.core.platform

import com.huskerdev.grapl.core.window.WindowPeer
import java.util.Collections.synchronizedList
import java.util.Collections.synchronizedSet
import kotlin.concurrent.thread

class BackgroundMessageHandler {
    companion object {
        var useHandler = true
        var continuousUpdate = true

        private val toInvoke = synchronizedList(arrayListOf<() -> Unit>())
        private var activePeers = synchronizedSet(hashSetOf<WindowPeer>())
        private var updatingThread: Thread? = null

        fun addPeer(peer: WindowPeer){
            activePeers.add(peer)
            checkState()
        }

        fun removePeer(peer: WindowPeer){
            activePeers.remove(peer)
        }

        fun invoke(runnable: () -> Unit){
            if(useHandler) {
                toInvoke.add(runnable)
                Platform.current.postEmptyMessage()
                checkState()
            }else
                runnable()
        }

        fun <T> invokeWaiting(runnable: () -> T): T{
            if(useHandler) {
                val sync = Object()
                var result: T? = null
                invoke {
                    result = runnable()
                    synchronized(sync) {
                        sync.notifyAll()
                    }
                }
                synchronized(sync) {
                    sync.wait()
                }
                return result!!
            }else
                return runnable()
        }

        private fun checkState(){
            if((activePeers.isNotEmpty() || toInvoke.isNotEmpty()) && updatingThread == null){
                updatingThread = thread(name = "Grapl Message Loop") {
                    val platform = Platform.current

                    while (activePeers.isNotEmpty() || toInvoke.isNotEmpty()) {
                        if(toInvoke.size > 0) {
                            toInvoke.forEach {
                                try {
                                    it()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            toInvoke.clear()
                        }
                        if(activePeers.size > 0) {
                            if (continuousUpdate)
                                platform.peekMessages()
                            else
                                platform.waitMessages()

                            activePeers.forEach {
                                try {
                                    it.dispatchUpdate()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    updatingThread = null
                }
            }
        }
    }
}