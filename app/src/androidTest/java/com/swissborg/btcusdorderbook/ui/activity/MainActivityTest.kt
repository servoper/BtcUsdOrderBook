package com.swissborg.btcusdorderbook.ui.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.swissborg.btcusdorderbook.R
import com.swissborg.btcusdorderbook.model.Ticker
import org.hamcrest.core.StringContains.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var activityActivityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun testTickerDataBinding() {
        val dailyChange = 0.5f
        val lastPrice = 0.6f
        val volume = 0.7f
        val high = 0.8f
        val low = 0.9f

        activityActivityTestRule.activity.mViewModel.ticker.postValue(
            Ticker(
                null, null, null, null, null,
                dailyChange, null, lastPrice, volume, high, low
            )
        )

        onView(withId(R.id.last_price)).check(matches(withText(containsString(lastPrice.toString()))))
        onView(withId(R.id.change)).check(matches(withText(containsString(dailyChange.toString()))))
        onView(withId(R.id.volume)).check(matches(withText(containsString(volume.toString()))))
        onView(withId(R.id.high)).check(matches(withText(containsString(high.toString()))))
        onView(withId(R.id.low)).check(matches(withText(containsString(low.toString()))))
    }
}