package com.example.audioplayer.di

import android.app.Application
import com.example.audioplayer.MainActivity
import com.example.audioplayer.services.AudioService
import com.example.audioplayer.services.ServiceModule
import dagger.BindsInstance
import dagger.Component

@Component(modules = [ViewModelModule::class, ServiceModule::class])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        fun build(): ApplicationComponent

        @BindsInstance
        fun application(application: Application): Builder

//        fun serviceModule(serviceModule: ServiceModule): Builder
    }

    fun inject(activity: MainActivity)
    fun inject(service: AudioService)
}