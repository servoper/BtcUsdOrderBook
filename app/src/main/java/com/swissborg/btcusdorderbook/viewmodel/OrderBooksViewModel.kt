package com.swissborg.btcusdorderbook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.swissborg.btcusdorderbook.model.OrderBook
import com.swissborg.btcusdorderbook.repository.OrderBookRepository

class OrderBooksViewModel(private val mRepository: OrderBookRepository = OrderBookRepository()) : ViewModel() {

    val askOrderBooks: LiveData<ArrayList<OrderBook>>
        get() = mRepository.getAskOrderBooks()


    val bidsOrderBooks: LiveData<ArrayList<OrderBook>>
        get() = mRepository.getBidOrderBooks()

    fun stopObserving() {
        mRepository.disconnectFromWebSocket()
    }

    fun startObserving() {
        mRepository.connectToWebSocket()
    }
}