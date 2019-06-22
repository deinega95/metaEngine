package com.meta_engine.common.network

import com.google.gson.Gson
import com.meta_engine.common.Coordinator
import com.meta_engine.common.utils.MyLog
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ErrorHandler @Inject constructor() {
    @Inject
    lateinit var coordinator: Coordinator
    @Inject
    lateinit var gson: Gson


    @Throws(IOException::class)
    fun checkBody(response: Response): Response {
        val contentType = response.body()?.contentType()
        val json = response.body()!!.string()
        MyLog.show("response body " + json)
        /* var error: ErrorFromApi? = null
         try {
             error = gson.fromJson(json, ErrorFromApi::class.java)
         } catch (ex: JsonSyntaxException) {
         }*/


        val body = ResponseBody.create(contentType, json)
        return response.newBuilder().body(body).build()
    }

    fun handle(e: Exception? = null) {
        MyLog.show("errorHandler " + e?.localizedMessage)
        //coordinator.showErrorFromIO(R.string.error_in_request)
    }

}