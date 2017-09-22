package com.github.droibit.android.livedata_viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import timber.log.Timber
import kotlin.coroutines.experimental.CoroutineContext

open class RunnableViewModel : ViewModel() {

    private var job: Job? = null

    val isActive: Boolean get() = job?.isActive == true

    protected fun ui(block: suspend CoroutineScope.() -> Unit) {
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

open class RunnableAndroidViewModel(
        application: Application,
        private val context: CoroutineContext = newSingleThreadContext("runnable-view-model"))
    : AndroidViewModel(application) {

    private var job: Job? = null

    val isActive: Boolean get() = job?.isActive == true

    protected fun ui(block: suspend CoroutineScope.() -> Unit) {
        job = launch(UI) {
            try {
                block()
            } catch (e: CancellationException) {
                Timber.w(e)
            }
        }
    }

    protected fun execute(block: suspend CoroutineScope.() -> Unit) {
        job = launch(context) {
            try {
                block()
            } catch (e: CancellationException) {
                Timber.w(e)
            }
        }
    }

    protected fun <T> CoroutineScope.async(context: CoroutineContext? = null, block: suspend CoroutineScope.() -> T): Deferred<T> {
        val c = coroutineContext.run { if (context != null) this + context else this }
        return kotlinx.coroutines.experimental.async(c, block = block)
    }

    fun cancel() {
        job?.let { if (it.isActive) it.cancel() }
    }

    override fun onCleared() {
        cancel()
    }
}