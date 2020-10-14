package com.example.audioplayer

interface Player {
    fun play()
    fun pause()
    fun rewind(seconds: Int)
    fun forward(seconds: Int)
}