package com.example.audioplayer.application

import android.app.Application
import com.example.audioplayer.di.ApplicationComponent
import com.example.audioplayer.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector

class AudioPlayerApplication : Application(), HasAndroidInjector {

    companion object {
        lateinit var appComponent: ApplicationComponent
            private set
    }

//    private val serviceModule: ServiceModule by lazy {  }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent
            .builder()
            .application(this)
//            .serviceModule(serviceModule)
            .build()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        TODO("Not yet implemented")
    }
}