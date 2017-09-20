package com.github.droibit.android.livedata_viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import timber.log.Timber

open class RunnableViewModel : ViewModel() {

    private var job: Job? = null

    val isActive: Boolean get() = job?.isActive == true

    protected fun ui(block: suspend CoroutineScope.()->Unit) {
        job = launch(UI) {
            try {
                block()
            } catch (e: CancellationException) {
                Timber.w(e)
            }
        }
    }

    protected fun <T> CoroutineScope.async(block: suspend CoroutineScope.() -> T)
            = kotlinx.coroutines.experimental.async(coroutineContext + kotlinx.coroutines.experimental.CommonPool, block = block)

    fun cancel() {
        job?.let { if (it.isActive) it.cancel() }
    }

    override fun onCleared() {
        cancel()
    }
}

open class RunnableAndroidViewModel(application: Application) : AndroidViewModel(application) {

    private var job: Job? = null

    val isActive: Boolean get() = job?.isActive == true

    protected fun ui(block: suspend CoroutineScope.()->Unit) {
        job = launch(UI) {
            try {
                block()
            } catch (e: CancellationException) {
                Timber.w(e)
            }
        }
    }

    protected fun <T> CoroutineScope.async(block: suspend CoroutineScope.() -> T)
            = kotlinx.coroutines.experimental.async(coroutineContext + kotlinx.coroutines.experimental.CommonPool, block = block)

    fun cancel() {
        job?.let { if (it.isActive) it.cancel() }
    }

    override fun onCleared() {
        cancel()
    }
}