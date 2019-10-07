package com.bemobile.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bemobile.databinding.ActivityProductListBinding
import com.bemobile.interfaceClasses.ProductListInterfaceView
import com.bemobile.presenter.ProductListPresenter
import com.bemobile.utils.Common
import com.google.gson.JsonArray
import com.bemobile.R
import com.bemobile.adapter.ProductListAdapter
import com.bemobile.model.Product
import com.bemobile.model.Rates
import com.bemobile.model.Transaction
import com.bemobile.network.RetrofitClass
import com.bemobile.network.RetrofitInterface
import com.google.gson.Gson
import com.google.gson.JsonElement
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject


class ProductListActivity : AppCompatActivity(), ProductListInterfaceView,
    ProductListAdapter.ProductListClickListner {


    lateinit var productArrayList: ArrayList<Product>//Product Array
    lateinit var ratesArrayList: ArrayList<Rates>//Rates Array

    lateinit var productListAdapter: ProductListAdapter//Product List Adapter

    lateinit var productListPresenter: ProductListPresenter
    lateinit var activityProductListBinding: ActivityProductListBinding
    var ratesCurrency = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProductListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_product_list)

        activityProductListBinding.layoutHeader.txtHeaderTitle.setText(getString(R.string.product).toUpperCase())

        ratesArrayList = ArrayList()
        productArrayList = ArrayList()
        productListAdapter = ProductListAdapter(productArrayList)
        activityProductListBinding.recycleViewProductList.adapter = productListAdapter
        productListAdapter.setProductListClickListner(this)

        activityProductListBinding.layoutNoInternetConnection.txtRefreshButton.setOnClickListener({
            getAllProductList()
        })

        getAllProductList()

    }

    fun getAllProductList() {
        if (Common.isNetwordAvailable(this)) {

            activityProductListBinding.shimmerViewProductList.startShimmerAnimation()
            activityProductListBinding.shimmerViewProductList.visibility = View.VISIBLE
            activityProductListBinding.layoutNoInternetConnection.layoutNoInternetConnectionMain.visibility =
                View.GONE

            Handler().postDelayed({
                getProductRates()
            }, 200)

        } else {
            activityProductListBinding.shimmerViewProductList.stopShimmerAnimation()
            activityProductListBinding.shimmerViewProductList.visibility = View.GONE
            activityProductListBinding.layoutNoInternetConnection.layoutNoInternetConnectionMain.visibility =
                View.VISIBLE
            Common.ShowToast(this@ProductListActivity, getString(R.string.no_internet_connection))
        }
    }

    public override fun onResume() {
        super.onResume()
        activityProductListBinding.shimmerViewProductList.startShimmerAnimation()
    }

    override fun onPause() {
        activityProductListBinding.shimmerViewProductList.stopShimmerAnimation()
        super.onPause()
    }

    /*Product Api Response*/
    override fun getProductListResponse(productArray: JsonArray) {

        for (pi in 0..(productArray.size() - 1)) {
            var productObj: JsonElement? = productArray.get(pi)
            var product: Product = Product()
            product.productName = productObj?.getAsJsonObject()?.get("sku")?.asString
            product.currency = productObj?.getAsJsonObject()?.get("currency")?.asString

            var amount: Float? = productObj?.getAsJsonObject()?.get("amount")?.asFloat
            product.productPrice = productObj?.getAsJsonObject()?.get("amount")?.asString

            var isAdded = false
            /*Add Product ont itme and calculate total price*/
            var convetCurAmount: Float = 0.0f
            if (!product.currency.equals("EUR")) {
                convetCurAmount = ratesConverterEur(product.currency!!, amount!!)
            }
            product.productTotalPrice = String.format("%.2f", convetCurAmount)

            if (productArrayList.size != 0) {
                var position: Int = 0
                for (pData in productArrayList) {
                    if (pData.productName.equals(product.productName)) {
                        var productAmount: Float = pData.productTotalPrice?.toFloat()!!
                        var productTotalAmount = amount?.plus(productAmount)
                        /*Add Transaction Value*/
                        var transaction: Transaction = Transaction()
                        transaction.sku = product.productName
                        transaction.amount = product.productPrice
                        transaction.currency = product.currency
                        pData.transationArray.add(transaction)

                        pData.productTotalPrice = String.format("%.2f", productTotalAmount)
                        productArrayList.set(position, pData)
                        isAdded = true
                        break
                    }
                    position++
                }
            }
            if (!isAdded)
                productArrayList.add(product)
        }


        Log.d("JsonArray", "found a duplicate JsonArray = " + productArrayList.size)
        productListAdapter.notifyDataSetChanged()

        activityProductListBinding.shimmerViewProductList.visibility = View.GONE
    }

    /*Product Api Error Response*/
    override fun getResponseError(resError: String) {
        Common.ShowErrorMessage(this, resError, null)
        activityProductListBinding.shimmerViewProductList.visibility = View.GONE
    }

    /*Price Converter*/
    fun ratesConverterEur(rate: String, amount: Float): Float {
        loop@ for (ratData in ratesArrayList) {
            if (rate.equals(ratData.from) && ratData.to.equals("EUR")) {
                var rateAmount: Float = ratData.rate!!.toFloat()
                ratesCurrency = amount * rateAmount
                break@loop
            }
        }

        if (ratesCurrency == 0.0f) {
            loop@ for (rData in ratesArrayList) {
                if (rate.equals(rData.from)) {
                    var rateAmount: Float = rData.rate!!.toFloat()
                    var amount = amount * rateAmount
                    ratesConverterEur(rData.to!!, amount)
                    break@loop
                }
            }
        }
        return ratesCurrency
    }

    /*Call Rate Api*/
    fun getProductRates() {
        var retrofitClient = RetrofitClass.getClient()
        var retrofitInterface = retrofitClient!!.create(RetrofitInterface::class.java)
        retrofitInterface.getAllRates()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleRatesResponse, this::handleError)
    }

    /*Api Rates Response*/
    private fun handleRatesResponse(ratesJsonArray: JsonArray) {
        for (pi in 0..(ratesJsonArray.size() - 1)) {
            var rateObj: JsonElement? = ratesJsonArray.get(pi)
            var rates: Rates = Rates()
            rates.from = rateObj?.getAsJsonObject()?.get("from")?.asString
            rates.to = rateObj?.getAsJsonObject()?.get("to")?.asString
            rates.rate = rateObj?.getAsJsonObject()?.get("rate")?.asString
            ratesArrayList.add(rates)
        }
        Log.d("ratesArrayList", "ratesArrayList = " + ratesArrayList.size)
        productListPresenter = ProductListPresenter(this)
        productListPresenter.getAllProductList()
    }

    /*Api Handler Error*/
    private fun handleError(error: Throwable) {
        Common.ShowErrorMessage(
            this,
            error.message!!,
            activityProductListBinding.layoutNoInternetConnection.layoutNoInternetConnectionMain
        )
        activityProductListBinding.shimmerViewProductList.visibility = View.GONE
    }

    /*Click item row function*/
    override fun ProductTransctaionPage(position: Int) {

        var product: Product = productArrayList.get(position)

        Common.transactionArray.addAll(product.transationArray)

        var pti = Intent(this, TransactionListActivity::class.java)
        pti.putExtra("product_name", product.productName)
        pti.putExtra("totla_amount", product.productTotalPrice)
        startActivity(pti)
    }
}
