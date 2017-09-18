package com.github.droibit.android.livedata_viewmodel

import android.arch.lifecycle.*

fun <X, Y> LiveData<X>.map(func: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, func)
}

fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, func)
}

inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T?) -> Unit) {
    observe(owner, Observer {
        observer(it)
    })
}

inline fun <reified T : ViewModel> ViewModelProvider.get(): T = get(T::class.java)