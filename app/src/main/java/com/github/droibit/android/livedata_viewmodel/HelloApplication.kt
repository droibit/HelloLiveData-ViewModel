package com.github.droibit.android.livedata_viewmodel

import android.app.Application
import timber.log.Timber


class HelloApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}