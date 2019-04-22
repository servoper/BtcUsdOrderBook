package com.swissborg.btcusdorderbook.model

data class OrderBook(var usdPrice: Float, var btcPrice: Float, var isPositive: Boolean = true)