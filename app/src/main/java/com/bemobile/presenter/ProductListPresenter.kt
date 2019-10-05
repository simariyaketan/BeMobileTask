package com.bemobile.presenter

import com.bemobile.interfaceClasses.ProductListInterface
import com.bemobile.interfaceClasses.ProductListInterfaceView
import com.bemobile.network.RetrofitClass
import com.bemobile.network.RetrofitInterface
import com.google.gson.JsonArray
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ProductListPresenter(var productListInterfaceView: ProductListInterfaceView) :
    ProductListInterface {


    override fun getAllProductList() {
        getProductObservable().subscribe(this::handleResponse, this::handleError)
    }

    fun getProductObservable(): Observable<JsonArray> {
        var retrofitClient = RetrofitClass.getClient()
        var retrofitInterface = retrofitClient!!.create(RetrofitInterface::class.java)
        return retrofitInterface.getAllProductList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun handleResponse(jsonArray: JsonArray) {
        productListInterfaceView.getProductListResponse(jsonArray)
    }

    private fun handleError(error: Throwable) {
        productListInterfaceView.getResponseError(error.localizedMessage)
    }

}