package com.bemobile.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bemobile.R
import com.bemobile.model.Transaction

object Common{

    var transactionArray : ArrayList<Transaction> = ArrayList()

    fun ShowToast(activity: Activity, Msg: String) {
        Toast.makeText(activity, Msg, Toast.LENGTH_LONG).show()
    }

    fun isNetwordAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun ShowErrorMessage(activity: Activity, Msg: String, internetView: View?) {
        Log.d("Msg", "Msg = " + Msg)
        var errorMessage: String = "Somthing went wrong."
        if (Msg.toLowerCase().contains("failed to connect to") || Msg.toLowerCase().contains("unable to resolve hos")) {
            errorMessage = activity.getString(R.string.no_internet_connection)
            if (internetView != null )
                internetView.visibility = View.VISIBLE
        } else if (Msg.toLowerCase().contains("http 500 internal server error"))
            errorMessage = "HTTP 500 Internal Server Error"
        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
    }
}