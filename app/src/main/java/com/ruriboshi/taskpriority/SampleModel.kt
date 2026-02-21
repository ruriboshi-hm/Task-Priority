package com.ruriboshi.taskpriority

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SampleModel:RealmObject() {
    @PrimaryKey
    var id = 0L
    var layoutID = 0L
}