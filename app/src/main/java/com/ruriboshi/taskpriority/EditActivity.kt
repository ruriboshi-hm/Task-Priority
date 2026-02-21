package com.ruriboshi.taskpriority

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.widget.DatePicker
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RadioButton
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class EditActivity : AppCompatActivity() ,DatePickerDialog.OnDateSetListener{
    private lateinit var realm: Realm
    var myYear = 0L
    var myMonth = 0L
    var myDay = 0L
    var btn1 = 0L
    var btn2 = 0L
    var btn3 = 0L
    var btn4 = 0L
    var calenderId = 0L

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val locale = Locale.getDefault()

        val editTextTarget:TextView = findViewById(R.id.editTextTarget)
        val editTextDate:TextView = findViewById(R.id.editTextDate)
        val btnSave:Button = findViewById(R.id.btnSave)
        val btnDel:ImageButton = findViewById(R.id.delButton)
        val btnFinish:Button = findViewById(R.id.btnFinish)
        val calenderBtn:ImageButton = findViewById(R.id.calenderBtn)
        val lineView:View = findViewById(R.id.lineView)

        val dateNullBtn:RadioButton = findViewById(R.id.dateNullBtn)
        val dateSetBtn:RadioButton = findViewById(R.id.dateBtn)

        val btn11:RadioButton = findViewById(R.id.Btn1)
        val btn12:RadioButton = findViewById(R.id.Btn2)
        val btn13:RadioButton = findViewById(R.id.Btn3)
        val btn14:RadioButton = findViewById(R.id.Btn4)
        val btn15:RadioButton = findViewById(R.id.Btn5)

        val btn21:RadioButton = findViewById(R.id.Btn2_1)
        val btn22:RadioButton = findViewById(R.id.Btn2_2)
        val btn23:RadioButton = findViewById(R.id.Btn2_3)
        val btn24:RadioButton = findViewById(R.id.Btn2_4)
        val btn25:RadioButton = findViewById(R.id.Btn2_5)

        realm = Realm.getDefaultInstance()

        createNotificationChannel()

        val getId = intent.getLongExtra("ID",0L)
        if(getId > 0){
            val myModelResult = realm.where<MyModel>()
                .equalTo("id",getId).findFirst()

            editTextTarget.text = myModelResult?.target.toString()
            if (myModelResult != null) { btn1 = myModelResult.priority }

            if (myModelResult != null) { btn2 = myModelResult.importance }

            if (myModelResult != null) { btn3 = myModelResult.difficultyLevel }

            if (myModelResult != null) { btn4 = myModelResult.troublesome }

            if (btn1 == 5L){ btn11.isChecked = true }else if (btn1 == 4L){ btn12.isChecked = true
            }else if (btn1 == 3L){ btn13.isChecked = true }else if (btn1 == 2L){ btn14.isChecked = true
            }else if (btn1 == 1L){ btn15.isChecked = true }

            if (btn2 == 5L){ btn21.isChecked = true }else if (btn2 == 4L){ btn22.isChecked = true
            }else if (btn2 == 3L){ btn23.isChecked = true }else if (btn2 == 2L){ btn24.isChecked = true
            }else if (btn2 == 1L){ btn25.isChecked = true }

            btnDel.visibility = View.VISIBLE

            if(myModelResult?.year == 0L){
                dateNullBtn.isChecked = true
                editTextDate.text = ""
                editTextDate.visibility = View.GONE
                calenderBtn.visibility = View.GONE
                lineView.visibility = View.GONE
            }else{
                dateSetBtn.isChecked = true
                val dateText = myModelResult?.year.toString() +"/"+ myModelResult?.month.toString()+"/" + myModelResult?.date.toString()
                editTextDate.text = dateText
                myYear = myModelResult?.year!!
                myMonth = myModelResult.month
                myDay = myModelResult.date
                editTextDate.visibility = View.VISIBLE
                calenderBtn.visibility = View.VISIBLE
                lineView.visibility = View.VISIBLE
            }
            btnFinish.visibility = View.VISIBLE

            if (myModelResult.taskFinished == 1L){
                if (locale == Locale.JAPAN){
                    btnFinish.text = "タスクを未完了にする"
                }else{
                    btnFinish.text = "Unfinished"
                }
            }else{
                if (locale == Locale.JAPAN){
                    btnFinish.text = "タスクを完了"
                }else{
                    btnFinish.text = "Finished"
                }
            }

        }else{
            btnDel.visibility = View.INVISIBLE
            dateNullBtn.isChecked = true
            editTextDate.visibility = View.GONE
            btn13.isChecked = true
            btn23.isChecked = true
            calenderBtn.visibility = View.GONE
            lineView.visibility = View.GONE
            btnFinish.visibility = View.GONE
        }

        dateNullBtn.setOnClickListener {
            editTextDate.visibility = View.GONE
            calenderBtn.visibility = View.GONE
            lineView.visibility = View.GONE
        }

        dateSetBtn.setOnClickListener {
            editTextDate.visibility = View.VISIBLE
            calenderBtn.visibility = View.VISIBLE
            lineView.visibility = View.VISIBLE
            if (myYear == 0L){
                val nowTime = LocalDate.now()
                myYear = nowTime.year.toLong()
                myMonth = nowTime.monthValue.toLong()
                myDay = nowTime.dayOfMonth.toLong()
            }
            val dateText = myYear.toString() +"/"+ myMonth.toString()+"/" + myDay.toString()
            editTextDate.text = dateText
        }

        btnSave.setOnClickListener {
            var target: String = ""

            btn1 = if (btn11.isChecked){ 5 }else if (btn12.isChecked){ 4 }else if (btn13.isChecked){ 3
            }else if (btn14.isChecked){ 2 }else{ 1 }

            btn2 = if (btn21.isChecked){ 5 }else if (btn22.isChecked){ 4 }else if (btn23.isChecked){ 3
            }else if (btn24.isChecked){ 2 }else{ 1 }

            btn3 = btn1+btn2

            if (!editTextTarget.text.isNullOrEmpty()) {
                target = editTextTarget.text.toString()

                if(getId == 0L) {
                    realm.executeTransaction {
                        val currentId = realm.where<MyModel>().max("id")
                        val nextId = (currentId?.toLong() ?: 0L) + 1L

                        val myModel = realm.createObject<MyModel>(nextId)
                        myModel.target = target
                        myModel.priority = btn1
                        myModel.importance = btn2
                        myModel.difficultyLevel = btn3
                        myModel.troublesome = btn4
                        if (dateNullBtn.isChecked){
                            myModel.year = 0L
                            myModel.month = 0L
                            myModel.date = 0L
                        }else{
                            myModel.year = myYear.toLong()
                            myModel.month = myMonth.toLong()
                            myModel.date = myDay.toLong()
                        }
                        if (myYear != 0L){
                            calenderId = nextId
                            scheduleNotification()
                        }
                    }
                }else{
                    realm.executeTransaction {
                        val myModel = realm.where<MyModel>()
                            .equalTo("id",getId).findFirst()
                        myModel?.target = target
                        myModel?.priority = btn1
                        myModel?.importance = btn2
                        myModel?.difficultyLevel = btn3
                        myModel?.troublesome = btn4
                        if (dateNullBtn.isChecked){
                            myModel?.year = 0L
                            myModel?.month = 0L
                            myModel?.date = 0L
                        }else{
                            myModel?.year = myYear.toLong()
                            myModel?.month = myMonth.toLong()
                            myModel?.date = myDay.toLong()
                        }
                        if (dateNullBtn.isChecked){
                            calenderId = getId
                            cancelSchedule()
                        }else{
                            if (myModel?.taskFinished == 0L){
                                calenderId = getId
                                scheduleNotification()
                            }
                        }
                    }
                }
                if (locale == Locale.JAPAN){
                    Toast.makeText(applicationContext,"保存しました",Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(applicationContext,"Saved",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }else{
                if (locale == Locale.JAPAN){
                    Toast.makeText(applicationContext,"タスク名を入力してください",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"Please enter a task title",Toast.LENGTH_SHORT).show()
                }

            }


        }
        btnDel.setOnClickListener {
            if (locale == Locale.JAPAN){
                AlertDialog.Builder(it.context)
                    .setTitle("本当に削除しますか？")
                    .setPositiveButton("削除",DialogInterface.OnClickListener { _, _ ->
                        val myModelResult = realm.where<MyModel>()
                            .equalTo("id",getId).findFirst()
                        realm.executeTransaction {
                            myModelResult?.deleteFromRealm()
                        }
                        calenderId = getId
                        cancelSchedule()
                        Toast.makeText(applicationContext,"削除しました",Toast.LENGTH_SHORT).show()
                        finish()
                    })
                    .setNegativeButton("戻る",null)
                    .show()
            }else{
                AlertDialog.Builder(it.context)
                    .setTitle("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete",DialogInterface.OnClickListener { _, _ ->
                        val myModelResult = realm.where<MyModel>()
                            .equalTo("id",getId).findFirst()
                        realm.executeTransaction {
                            myModelResult?.deleteFromRealm()
                        }
                        calenderId = getId
                        cancelSchedule()
                        Toast.makeText(applicationContext,"Deleted",Toast.LENGTH_SHORT).show()
                        finish()
                    })
                    .setNegativeButton("Cancel",null)
                    .show()
            }
        }

        btnFinish.setOnClickListener() {
            var target:String = ""
            if(!editTextTarget.text.isNullOrEmpty()){
                target = editTextTarget.text.toString()
            }
            val myModelResult = realm.where<MyModel>().equalTo("id",getId).findFirst()
            realm.executeTransaction {
                val fTask = myModelResult?.taskFinished
                if (fTask == 0L){
                    myModelResult.taskFinished = 1L

                }else{
                    myModelResult?.taskFinished = 0L
                }
                if (dateNullBtn.isChecked){
                    myModelResult?.target = target
                    myModelResult?.year = 0L
                    myModelResult?.month = 0L
                    myModelResult?.date = 0L
                }else{
                    myModelResult?.target = target
                    myModelResult?.year = myYear.toLong()
                    myModelResult?.month = myMonth.toLong()
                    myModelResult?.date = myDay.toLong()
                }


            }
            if(myModelResult?.taskFinished == 1L){
                calenderId = getId
                cancelSchedule()
                if (locale == Locale.JAPAN){
                    Toast.makeText(applicationContext,"タスクを完了しました",Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(applicationContext,"Finished the task",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }else{
                if (!dateNullBtn.isChecked){
                    calenderId = getId
                    scheduleNotification()
                }
                if (locale == Locale.JAPAN){
                    Toast.makeText(applicationContext,"タスクを未完了にしました",Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(applicationContext,"Unfinished the task",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        editTextDate.setOnClickListener {
            val newFragment = DatePick()
            newFragment.myYear = myYear.toInt()
            newFragment.myMonth = myMonth.toInt() - 1
            newFragment.myDay = myDay.toInt()
            newFragment.show(supportFragmentManager, "datePicker")
        }

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }
    }

    private fun cancelSchedule() {
        /*val getId = intent.getLongExtra("ID",0L)
        val myTaskModelResult = realm.where<MyTaskModel>()
            .equalTo("id",getId).findFirst()*/
        val sIntent = Intent(applicationContext, AlarmReceiver::class.java)
        val nID = calenderId.toInt()
        val pending = PendingIntent.getBroadcast(
            applicationContext,
            nID,
            sIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pending)

    }

    private fun scheduleNotification() {
        val scheduleIntent = Intent(applicationContext, AlarmReceiver::class.java)
        val taskText:TextView = findViewById(R.id.editTextTarget)
        /*val getId = intent.getLongExtra("ID",0L)
        val myTaskModelResult = realm.where<MyTaskModel>()
            .equalTo("id",getId).findFirst()*/
        val nfID = calenderId.toInt()
        val locale = Locale.getDefault()
        if (locale == Locale.JAPAN) {
            scheduleIntent.putExtra("titleExtra","今日のタスク")
        }else{
            scheduleIntent.putExtra("titleExtra","Today's task")
        }
        scheduleIntent.putExtra("messageExtra",taskText.text.toString())
        scheduleIntent.putExtra("notificationId", nfID)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            nfID,
            scheduleIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val sTime = getTime()
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,sTime,pendingIntent)
    }

    private fun getTime():Long {
        val sCal = Calendar.getInstance()
        sCal.set(myYear.toInt(),(myMonth-1).toInt(),myDay.toInt(),7,0)
        return sCal.timeInMillis
    }

    private fun createNotificationChannel() {
        val locale = Locale.getDefault()
        if (locale == Locale.JAPAN) {
            // 日本語環境
            val name = "タスク通知"
            val desc = "今日のタスクを通知します。"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = desc
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        } else {
            // その他の言語環境
            val name = "Task notification"
            val desc = "Notify the tasks today."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = desc
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDateSet(view:DatePicker?, year: Int, month: Int, date: Int){
        realm.executeTransaction {
            myYear = year.toLong()
            myMonth = month.toLong() + 1L
            myDay = date.toLong()
            val dateText = myYear.toString() +"/"+ myMonth.toString()+"/" + myDay.toString()
            val editTextDate:TextView = findViewById(R.id.editTextDate)
            editTextDate.text = dateText
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}