package com.swissborg.btcusdorderbook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.swissborg.btcusdorderbook.model.Ticker
import com.swissborg.btcusdorderbook.repository.TickerRepository

class TickerViewModel(private val mRepository: TickerRepository = TickerRepository()) : ViewModel() {

    val ticker: LiveData<Ticker>
        get() = mRepository.getTicker()

    fun stopObserving() {
        mRepository.disconnectFromWebSocket()
    }

    fun startObserving() {
        mRepository.connectToWebSocket()
    }
}