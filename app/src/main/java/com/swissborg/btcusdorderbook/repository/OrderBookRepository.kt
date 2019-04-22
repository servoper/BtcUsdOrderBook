package com.swissborg.btcusdorderbook.repository

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.swissborg.btcusdorderbook.model.OrderBook
import okhttp3.*
import okio.ByteString

const val WEB_SERVICE_URL = "wss://api.bitfinex.com/ws"

private const val MISSING_DELIMITER = "missingDelimiter"
private const val MESSAGE_SUBSCRIBE_BTCUSD_ORDER_BOOK =
    "{\"event\":\"subscribe\", \"channel\":\"book\", \"pair\":\"BTCUSD\", \"prec\":\"P0\" ,\"freq\":\"F1\"}"
private const val BOOKS_SEPARATOR_MESSAGE = "\"hb\""

class OrderBookRepository {

    private var mCollectingBooks: Boolean = false

    private val mTempAsks: ArrayList<OrderBook> = ArrayList()

    private val mTempBids: ArrayList<OrderBook> = ArrayList()

    //Negative
    private var mAskOrderBooksObservable: MutableLiveData<ArrayList<OrderBook>> = MutableLiveData()

    //Positive
    private var mBidOrderBooksObservable: MutableLiveData<ArrayList<OrderBook>> = MutableLiveData()

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

    fun getAskOrderBooks(): LiveData<ArrayList<OrderBook>> {
        return mAskOrderBooksObservable
    }

    fun getBidOrderBooks(): LiveData<ArrayList<OrderBook>> {
        return mBidOrderBooksObservable
    }

    private fun getWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(MESSAGE_SUBSCRIBE_BTCUSD_ORDER_BOOK)
            }

            override fun onMessage(webSocket: WebSocket?, text: String?) {
                if (!TextUtils.isEmpty(text)) {
                    when {
                        isOrderBook(text!!) -> {
                            mCollectingBooks = true
                            onOrderBookReceived(text)
                        }

                        BOOKS_SEPARATOR_MESSAGE in text && mCollectingBooks -> {
                            updateWithCollectedOrderBooks()
                        }

                        BOOKS_SEPARATOR_MESSAGE in text && !mCollectingBooks -> {
                            mCollectingBooks = true
                        }

                        !text.matches(Regex(".*[a-zA-Z].*")) && BOOKS_SEPARATOR_MESSAGE !in text -> {
                            updateOrderBooks(text)
                        }
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

    private fun isOrderBook(text: String): Boolean {
        return try {
            Gson().fromJson(text, Array<Float>::class.java)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun updateWithCollectedOrderBooks() {
        mAskOrderBooksObservable.postValue(sortByUsdDescending(ArrayList(mTempAsks)))
        mBidOrderBooksObservable.postValue(sortByUsdDescending(ArrayList(mTempBids)))
        mTempAsks.clear()
        mTempBids.clear()
        mCollectingBooks = false
    }

    private fun sortByUsdDescending(list: ArrayList<OrderBook>): ArrayList<OrderBook> {
        list.sortBy { it.usdPrice }
        list.reverse()
        return list
    }

    private fun onOrderBookReceived(text: String) {
        val orderBookArray = Gson().fromJson(text, Array<Float>::class.java)
        when {
            orderBookArray[3] < 0 -> mTempAsks.add(OrderBook(orderBookArray[1], -orderBookArray[3], false))
            else -> mTempBids.add(OrderBook(orderBookArray[1], orderBookArray[3]))
        }
    }

    private fun updateOrderBooks(text: String?) {
        var parsedText = text!!.substringAfter(",", MISSING_DELIMITER)
        if (parsedText != MISSING_DELIMITER) {
            parsedText = parsedText.substring(0, parsedText.length - 1)

            val orderBooksArray = Gson().fromJson(parsedText, Array<Array<Float>>::class.java)

            val tempAsks = ArrayList<OrderBook>()
            val tempBids = ArrayList<OrderBook>()

            orderBooksArray.forEach {
                // <0 is ask documented here: https://docs.bitfinex.com/v1/reference#ws-public-order-books
                when {
                    it[2] < 0 ->
                        // we need the positive value, because the sign is only to determinate the difference
                        tempAsks.add(OrderBook(it[0], -it[2], false))
                    else -> tempBids.add(OrderBook(it[0], it[2]))
                }
            }

            mAskOrderBooksObservable.postValue(sortByUsdDescending(tempAsks))
            mBidOrderBooksObservable.postValue(sortByUsdDescending(tempBids))
        }
    }
}