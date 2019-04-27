package com.swissborg.btcusdorderbook.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swissborg.btcusdorderbook.model.Ticker
import com.swissborg.btcusdorderbook.repository.TickerRepository

class TickerViewModel(private val mRepository: TickerRepository = TickerRepository()) : ViewModel() {

    var ticker: MutableLiveData<Ticker> = mRepository.getTicker()

    fun stopObserving() {
        mRepository.disconnectFromWebSocket()
    }

    fun startObserving() {
        mRepository.connectToWebSocket()
    }
}