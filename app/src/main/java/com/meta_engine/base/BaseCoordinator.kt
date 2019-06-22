package com.meta_engine.base

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.annotation.StringRes
import com.meta_engine.common.utils.MyLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


abstract class BaseCoordinator {
    var progressStack: Int = 0
    private var routerView: WeakReference<IRouterView>? = null

    fun setCurrentView(view: IRouterView) {
        MyLog.show("set current view")
        routerView = WeakReference(view)
    }

    fun clear(view: IRouterView) {
        if (routerView?.get() == view)
            routerView = null
    }

    fun showMessage(@StringRes content: Int) {
        routerView?.get()?.showMessage(content)
    }

    fun showMessage(@StringRes content: Int, callback: () -> Unit) {
        routerView?.get()?.showMessage(content, callback)
    }

    fun showMessage(content: String, callback: () -> Unit = {}) {
        routerView?.get()?.showMessage(content, callback)
    }

    fun showProgress() {
        MyLog.show("show progress owner " + routerView?.get())
        progressStack++
        routerView?.get()?.showProgress()
    }

    fun hideProgress() {
        if (progressStack > 0) progressStack--
        if (progressStack == 0) routerView?.get()?.hideProgress()
    }

    fun showError(message: Int, onPositive: () -> Unit? = { null }) {
        routerView?.get()?.showError(message, onPositive)
    }

    fun showError(message: String, onPositive: () -> Unit? = { null }) {
        routerView?.get()?.showError(message, onPositive)
    }

    fun showProgressFromIO() = GlobalScope.launch(Dispatchers.Main) {
        showProgress()
    }


    fun hideProgressFromIO() = GlobalScope.launch(Dispatchers.Main) {
        hideProgress()
    }

    fun showErrorFromIO(message: Int) = GlobalScope.launch(Dispatchers.Main) {
        showError(message)
    }

    fun showErrorFromIO(message: String) = GlobalScope.launch(Dispatchers.Main) {
        showError(message)
    }

    fun navigate(@IdRes id: Int, args: Bundle? = null, output: BaseFragmentOutput? = null) {
        routerView?.get()?.navigate(id, args, output)
    }

    fun setFlow(@NavigationRes id: Int, args: Bundle? = null, output: BaseFragmentOutput? = null) {
        routerView?.get()?.setFlow(id, args, output)
        onFlowChanged(id)

    }

    fun startActivity(clazz: Class<*>) {
        routerView?.get()?.startActivity(clazz)
    }


    abstract protected fun onFlowChanged(@NavigationRes id: Int)


}

interface IRouterView {

    fun showMessage(@StringRes content: Int)
    fun showMessage(@StringRes content: Int, callback: () -> Unit)
    fun showMessage(content: String, callback: () -> Unit = {})
    fun showProgress()
    fun showError(message: Int, onPositive: () -> Unit? = { null })
    fun showError(message: String, onPositive: () -> Unit? = { null })
    fun hideProgress()
    fun setFlow(@NavigationRes id: Int, args: Bundle?, output: BaseFragmentOutput?)
    fun navigate(@IdRes id: Int, args: Bundle?, output: BaseFragmentOutput?)
    fun startActivity(clazz: Class<*>)
}