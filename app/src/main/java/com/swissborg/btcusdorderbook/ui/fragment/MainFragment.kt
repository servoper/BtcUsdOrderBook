package com.swissborg.btcusdorderbook.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.swissborg.btcusdorderbook.R
import com.swissborg.btcusdorderbook.databinding.FragmentMainBinding
import com.swissborg.btcusdorderbook.ui.adapter.OrderBooksAdapter
import com.swissborg.btcusdorderbook.viewmodel.OrderBooksViewModel
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private lateinit var mBinding: FragmentMainBinding
    private lateinit var mViewModel: OrderBooksViewModel

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        mBinding.lifecycleOwner = this

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel = ViewModelProviders.of(this)[OrderBooksViewModel::class.java]

        bid_list.hasFixedSize()
        ask_list.hasFixedSize()

        mViewModel.askOrderBooks.observe(this, Observer {
            when {
                ask_list.adapter == null -> ask_list.adapter =
                    OrderBooksAdapter(it)
                else -> (ask_list.adapter as OrderBooksAdapter).setOrderBooksList(it)
            }
            ask_list.adapter?.notifyDataSetChanged()

        })

        mViewModel.bidsOrderBooks.observe(this, Observer {
            when {
                bid_list.adapter == null -> bid_list.adapter =
                    OrderBooksAdapter(it)
                else -> (bid_list.adapter as OrderBooksAdapter).setOrderBooksList(it)
            }
            bid_list.adapter?.notifyDataSetChanged()
        })

        mBinding.viewModel = mViewModel
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