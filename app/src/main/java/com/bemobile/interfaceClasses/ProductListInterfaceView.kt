package com.bemobile.interfaceClasses

import com.google.gson.JsonArray

interface ProductListInterfaceView {
    fun getProductListResponse(jsonArray: JsonArray)
    fun getResponseError(resError: String)
}
