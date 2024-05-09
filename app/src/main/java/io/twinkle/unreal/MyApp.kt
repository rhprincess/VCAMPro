package io.twinkle.unreal

import android.app.Application

lateinit var vcampApp: MyApp
class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        vcampApp = this
    }
}