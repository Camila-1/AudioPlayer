package com.example.audioplayer.di

import android.app.Application
import com.example.audioplayer.MainActivity
import com.example.audioplayer.services.AudioService
import com.example.audioplayer.services.ServiceModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class, ServiceModule::class, CoreModule::class])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        fun build(): ApplicationComponent

        @BindsInstance
        fun application(application: Application): Builder

        fun coreModule(coreModule: CoreModule): Builder
    }

    fun inject(activity: MainActivity)
    fun inject(service: AudioService)
}