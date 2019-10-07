package com.bemobile.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.lang.reflect.Type
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bemobile.R
import com.bemobile.adapter.TransactionListAdapter
import com.bemobile.databinding.ActivityTransactionListBinding
import com.bemobile.model.Product
import com.bemobile.model.Transaction
import com.bemobile.utils.Common
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TransactionListActivity : AppCompatActivity() {

    lateinit var activityTransactionListBinding : ActivityTransactionListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTransactionListBinding = DataBindingUtil.setContentView(this,R.layout.activity_transaction_list)

        var productName = intent.getStringExtra("product_name")
        var totalAmount = intent.getStringExtra("totla_amount")

        activityTransactionListBinding.layoutHeader.imgBack.visibility = View.VISIBLE
        activityTransactionListBinding.layoutHeader.imgBack.setOnClickListener({
            finish()
        })

        activityTransactionListBinding.layoutHeader.txtHeaderTitle.setText(productName)
        activityTransactionListBinding.txtTotalAmount.setText("EUR " + totalAmount)

        var transactionListAdapter : TransactionListAdapter = TransactionListAdapter(Common.transactionArray)
        activityTransactionListBinding.recycleViewTransactionList.adapter = transactionListAdapter
        transactionListAdapter.notifyItemChanged(Common.transactionArray.size)
    }

    override fun onDestroy() {
        super.onDestroy()
        Common.transactionArray.clear()
    }
}
