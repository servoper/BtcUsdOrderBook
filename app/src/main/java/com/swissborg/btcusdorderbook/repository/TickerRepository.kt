package com.swissborg.btcusdorderbook.repository

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.swissborg.btcusdorderbook.model.Ticker
import okhttp3.*
import okio.ByteString

private const val MESSAGE_SUBSCRIBE_BTCUSD_TICKER =
    "{\"event\":\"subscribe\", \"channel\":\"ticker\", \"pair\":\"BTCUSD\"}"

class TickerRepository {

    private var mTickerObservable: MutableLiveData<Ticker> = MutableLiveData()

    private var mWebSocket: WebSocket? = null

    init {
        connectToWebSocket()
    }

    fun connectToWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder().url(WEB_SERVICE_URL).build()

        mWebSocket = client.newWebSocket(request, getWebSocketListener())
        client.dispatcher().executorService().shutdown()
    }

    fun disconnectFromWebSocket() {
        mWebSocket?.close(1000, null)
        mWebSocket?.cancel()
        mWebSocket = null
    }

    fun getTicker(): LiveData<Ticker> {
        return mTickerObservable
    }

    private fun getWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(MESSAGE_SUBSCRIBE_BTCUSD_TICKER)
            }

            override fun onMessage(webSocket: WebSocket?, text: String?) {
                if (!TextUtils.isEmpty(text)) {
                    try {
                        val tickerArray = Gson().fromJson(text, Array<Float>::class.java)
                        if (tickerArray.size == 11) {
                            mTickerObservable.postValue(Ticker(tickerArray))
                        }
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
                webSocket.close(1000, null)
                webSocket.cancel()
            }
        }
    }
}