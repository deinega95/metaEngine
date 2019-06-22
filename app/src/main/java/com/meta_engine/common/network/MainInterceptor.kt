package com.meta_engine.common.network

import com.meta_engine.common.Coordinator
import com.meta_engine.common.di.ComponentsHolder
import com.meta_engine.common.utils.MyLog
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import javax.inject.Inject


class MainInterceptor : Interceptor {
    companion object {
        const val TAG = "network"
    }

    @Inject
    lateinit var coordinator: Coordinator
    @Inject
    lateinit var errorHandler: ErrorHandler

    init {
        ComponentsHolder.applicationComponent.inject(this)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        val url = chain.request().url().toString()
        var response: Response?
        try {
            MyLog.show("request " + chain.request().url())
            response = chain.proceed(builder.build())
        } catch (ex: Exception) {
            if (ex.localizedMessage != "Canceled")
                errorHandler.handle(ex)
            throw ex
        }

        return errorHandler.checkBody(response)
    }



    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }
    }
}