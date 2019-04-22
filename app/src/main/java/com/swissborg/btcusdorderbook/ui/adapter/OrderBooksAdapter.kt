package com.swissborg.btcusdorderbook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.swissborg.btcusdorderbook.R
import com.swissborg.btcusdorderbook.databinding.ListItemOrderBookBinding
import com.swissborg.btcusdorderbook.model.OrderBook

class OrderBooksAdapter(private var mOrderBooks: ArrayList<OrderBook>) :
    RecyclerView.Adapter<OrderBooksAdapter.OrderBooksViewHolder>() {

    inner class OrderBooksViewHolder(val binding: ListItemOrderBookBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: OrderBooksViewHolder, position: Int) {
        holder.binding.orderBook = mOrderBooks[position]
    }

    override fun getItemCount(): Int = mOrderBooks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderBooksViewHolder =
        OrderBooksViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_order_book,
                parent,
                false
            )
        )

    fun setOrderBooksList(newList: ArrayList<OrderBook>) {
        when {
            mOrderBooks.isEmpty() -> {
                mOrderBooks = newList
                notifyItemRangeInserted(0, newList.size)
            }
            else -> {
                val result: DiffUtil.DiffResult = differencesResult(newList)
                mOrderBooks = newList
                result.dispatchUpdatesTo(this)
            }
        }
    }

    // This can be moved in the model
    private fun differencesResult(newList: ArrayList<OrderBook>): DiffUtil.DiffResult =
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            /**
             * Since we don't have unique identifier, we cannot understand if
             * the two items are same order book with changed content.
             * This is why I need to check all fields
             */
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                areItemsSame(oldItemPosition, newItemPosition)

            override fun getOldListSize(): Int = mOrderBooks.size

                override fun getNewListSize(): Int = newList.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areItemsSame(oldItemPosition, newItemPosition)
            }

            private fun areItemsSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return mOrderBooks[oldItemPosition].btcPrice == newList[newItemPosition].btcPrice &&
                        mOrderBooks[oldItemPosition].usdPrice == newList[newItemPosition].usdPrice &&
                        mOrderBooks[oldItemPosition].isPositive == newList[newItemPosition].isPositive
            }
        })
}