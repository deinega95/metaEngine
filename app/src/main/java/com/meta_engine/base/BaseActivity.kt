package com.meta_engine.base

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.meta_engine.R
import com.meta_engine.common.Coordinator
import com.meta_engine.common.di.ComponentsHolder
import com.meta_engine.common.utils.MyLog
import com.meta_engine.common.utils.Utils
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), IRouterView, ResultHandler {


    companion object {
        const val FRAGMENT_RESULT = "fragment_result"
    }

    private var output: BaseFragmentOutput? = null
    private var keyboardState = false
    private lateinit var navController: NavController

    @Inject
    lateinit var coordinator: Coordinator

    private var progress: MaterialDialog? = null

    init {
        ComponentsHolder.applicationComponent.inject(this)
    }


    override fun showMessage(@StringRes content: Int) {
        showMessage(getString(content))
    }

    override fun showMessage(@StringRes content: Int, callback: () -> Unit) {
        showMessage(getString(content), callback)
    }

    override fun showMessage(content: String, callback: () -> Unit) {
        MaterialDialog(this).show {
            message(text = content)
            positiveButton(R.string.ok) { callback.invoke() }
                .show()
        }
    }


    override fun showProgress() {
        keyboardState = Utils.hideSoftKeyboard(this)
        progress?.show()
        MyLog.show("progress show ${coordinator.progressStack}")
    }


    override fun showError(message: Int, onPositive: () -> Unit?) {
        showError(getString(message), onPositive)
    }


    override fun showError(message: String, onPositive: () -> Unit?) {
        MaterialDialog(this).show {
            title(R.string.error)
            message(text = message)
            cancelable(false)
            positiveButton(R.string.ok) {
                onDialogOkClick()
                onPositive.invoke()
            }
        }
    }

    protected open fun onDialogOkClick() {}


    override fun hideProgress() {
        progress?.cancel()
        val curFocus = currentFocus
        if (keyboardState && curFocus is EditText) Utils.showSoftKeyboard(this, curFocus)
    }


    override fun handleResult(args: Bundle) {
        MyLog.show("handle result")
        output?.onOutput(args)
        output = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progress = MaterialDialog(this).customView(R.layout.dialog_progress, scrollable = false).cancelable(false)
        if (coordinator.progressStack > 0) progress?.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (progress != null) {
            progress!!.dismiss();
            progress = null;
        }
        coordinator.clear(this)
    }

    override fun onStart() {
        super.onStart()
        if (coordinator.progressStack > 0) progress?.show()
    }

    override fun onResume() {
        super.onResume()
        coordinator.setCurrentView(this)
    }

    override fun onStop() {
        super.onStop()
        progress?.hide()
    }

    protected fun setNavController(navController: NavController) {
        this.navController = navController
    }


    override fun navigate(@IdRes id: Int, args: Bundle?, output: BaseFragmentOutput?) {
        Utils.hideSoftKeyboard(this)
        navController.navigate(id, args)
        title = navController.currentDestination?.label
        if (output != null) setOutput(output)
    }

    private fun setOutput(output: BaseFragmentOutput?) {
        this.output = output
    }

    override fun setFlow(@NavigationRes id: Int, args: Bundle?, output: BaseFragmentOutput?) {
        Utils.hideSoftKeyboard(this)
        navController.setGraph(id, args)
        title = navController.currentDestination?.label
        if (output != null) setOutput(output)
    }

    override fun startActivity(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        title = navController.currentDestination?.label
    }
}