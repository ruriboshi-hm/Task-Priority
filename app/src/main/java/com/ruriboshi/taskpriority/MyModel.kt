package com.ruriboshi.taskpriority

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MyModel :RealmObject(){
    @PrimaryKey
    var id:Long =0
    var target:String =""
    var taskFinished:Long = 0
    var year:Long = 0
    var month:Long = 0
    var date:Long = 0
    var priority:Long = 0
    var importance:Long = 0
    var difficultyLevel:Long = 0
    var troublesome:Long = 0
}