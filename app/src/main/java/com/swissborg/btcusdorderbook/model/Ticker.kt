package com.swissborg.btcusdorderbook.model

import kotlin.math.roundToInt

data class Ticker(
    val channelId: Int,    //CHANNEL_ID integer	Channel ID
    val bid: Float?, //BID	float	Price of last highest bid
    val bidSize: Float?,//BID_SIZE	float	Size of the last highest bid
    val ask: Float,//ASK	float	Price of last lowest ask
    val askSize: Float?,//ASK_SIZE	float	Size of the last lowest ask
    val dailyChange: Float?,//DAILY_CHANGE	float	Amount that the last price has changed since yesterday
    val dailyChangePerc: Float?,//DAILY_CHANGE_PERC	float	Amount that the price has changed expressed in percentage terms
    val lastPrice: Float?,//LAST_PRICE	float	Price of the last trade.
    val volume: Float?,//VOLUME	float	Daily volume
    val high: Float?,//HIGH	float	Daily high
    val low: Float?//LOW	float	Daily low
) {
    constructor(array: Array<Float>) : this(
        array[0].roundToInt(), array[1], array[2], array[3], array[4],
        array[5], array[6], array[7], array[8], array[9], array[10]
    )
}