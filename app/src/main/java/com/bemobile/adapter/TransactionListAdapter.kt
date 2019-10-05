package com.bemobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bemobile.R
import com.bemobile.databinding.ItemTransactionListBinding
import com.bemobile.model.Transaction

class TransactionListAdapter(val transactionArray: ArrayList<Transaction>) :
    RecyclerView.Adapter<TransactionListAdapter.TransactionListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListViewHolder {
        var itemTransactionListBinding: ItemTransactionListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_transaction_list, parent, false
        )
        var transactionListViewHolder: TransactionListViewHolder =
            TransactionListViewHolder(itemTransactionListBinding)
        return transactionListViewHolder
    }

    override fun getItemCount(): Int {
        return transactionArray.size
    }

    override fun onBindViewHolder(holder: TransactionListViewHolder, position: Int) {
        var transaction: Transaction = transactionArray.get(position)
        holder.Bind(transaction)
    }

    inner class TransactionListViewHolder(var transactionListViewHolder: ItemTransactionListBinding) :
        RecyclerView.ViewHolder(transactionListViewHolder.root) {
        fun Bind(transaction: Transaction) {
            transactionListViewHolder.transaction = transaction
        }
    }

}