package com.meta_engine.base

import com.meta_engine.common.utils.MyLog
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext


abstract class BasePresenter<V> : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job + exceptionHanler


    private val job by lazy { SupervisorJob() }

    private val exceptionHanler by lazy {
        CoroutineExceptionHandler { coroutineContext: CoroutineContext, throwable: Throwable ->
            MyLog.show("coroutine exception " + throwable.localizedMessage)

        }
    }

    protected var view: V? = null


    fun viewReady(view: V) {
        this.view = view
        viewAttached()
    }

    fun viewDied(view: V) {
        if (this.view === view) {
            this.view = null
            viewDettached()
        }
    }

    protected abstract fun viewAttached()

    protected abstract fun viewDettached()

}