package com.example.audioplayer.utils

import java.util.concurrent.TimeUnit

class Utils {

    companion object {
        fun secondsToMS(seconds: Long): String {
            return String.format(
                "%02d:%02d", TimeUnit.SECONDS.toMinutes(seconds),
                TimeUnit.SECONDS.toSeconds(seconds)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
            )
        }
    }
}
