package com.github.droibit.android.livedata_viewmodel

import android.app.Application
import timber.log.Timber


class HelloApplication : Application() {

    lateinit var pocketRepository: PocketRepository
        private set

    override fun onCreate() {
        super.onCreate()

        pocketRepository = PocketRepository(this)
        Timber.plant(Timber.DebugTree())
    }
}