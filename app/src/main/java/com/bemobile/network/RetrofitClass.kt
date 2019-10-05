package com.bemobile.network

import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClass {
    var BASE_URL = "http://quiet-stone-2094.herokuapp.com/"

    private lateinit var adapter: Retrofit

    var okClient = OkHttpClient.Builder().authenticator { route, response ->
        response.request().newBuilder()
            .header("Connection", "close")
            //.header("Content-Type", "application/json")
            .build()
    }
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .addNetworkInterceptor(StethoInterceptor())
        .build()

    fun getClient(): Retrofit? {

        Log.d("BASE_URL", "BASE_URL = $BASE_URL")
        adapter = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okClient)
            .build()


        return adapter
    }
}