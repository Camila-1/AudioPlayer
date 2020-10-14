package com.example.audioplayer.application

import android.app.Application
import com.example.audioplayer.di.ApplicationComponent
import com.example.audioplayer.di.CoreModule
import com.example.audioplayer.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector

class AudioPlayerApplication : Application(){

    companion object {
        lateinit var appComponent: ApplicationComponent
            private set
    }

    private val coreModule: CoreModule by lazy { CoreModule(this) }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent
            .builder()
            .application(this)
            .coreModule(coreModule)
            .build()
    }
}