package com.bemobile.network

import com.google.gson.JsonArray
import io.reactivex.Observable
import retrofit2.http.GET

interface RetrofitInterface {

    /*Get Rates*/
    @GET("rates.json")
    fun getAllRates(): Observable<JsonArray>

    /*Get Product List*/
    @GET("transactions.json")
    fun getAllProductList(): Observable<JsonArray>
}