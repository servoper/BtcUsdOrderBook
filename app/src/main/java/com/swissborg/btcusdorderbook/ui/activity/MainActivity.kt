package com.swissborg.btcusdorderbook.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.swissborg.btcusdorderbook.R
import com.swissborg.btcusdorderbook.databinding.ActivityMainBinding
import com.swissborg.btcusdorderbook.ui.fragment.MainFragment
import com.swissborg.btcusdorderbook.viewmodel.TickerViewModel

class MainActivity : AppCompatActivity() {

    lateinit var mViewModel: TickerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.order_books_container, MainFragment.newInstance())
                .commitNow()
        }

        mViewModel = ViewModelProviders.of(this)[TickerViewModel::class.java]

        binding.viewModel = mViewModel
    }

    override fun onPause() {
        super.onPause()
        mViewModel.stopObserving()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.startObserving()
    }
}