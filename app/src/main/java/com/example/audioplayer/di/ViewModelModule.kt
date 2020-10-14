package com.example.audioplayer.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.audioplayer.AudioPlayerViewModel
import com.example.audioplayer.Player
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
class ViewModelModule {

    @Singleton
    @Provides
    fun provideViewModelFactory(
        providers: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return requireNotNull(providers[modelClass as Class<out ViewModel>]).get() as T
            }
        }
    }

    @Provides
    @IntoMap
    @ViewModelKey(AudioPlayerViewModel::class)
    fun provideViewModel(): ViewModel {
        return AudioPlayerViewModel()
    }
}