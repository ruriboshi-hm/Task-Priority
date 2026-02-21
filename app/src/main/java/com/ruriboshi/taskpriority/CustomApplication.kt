package com.ruriboshi.taskpriority

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class CustomApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val config = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .build()
        Realm.setDefaultConfiguration(config)
    }

}