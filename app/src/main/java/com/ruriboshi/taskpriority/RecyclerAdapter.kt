package com.ruriboshi.taskpriority

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale


class RecyclerAdapter(realmResults: RealmResults<MyModel>):RecyclerView.Adapter<ViewHolderItem>() {
    private val rResults:RealmResults<MyModel> = realmResults
    private lateinit var realm: Realm
    private var calenderId = 0L
    private var taskText = ""
    private var taskYear = 0L
    private var taskMonth = 0L
    private var taskDay = 0L


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val oneXml = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_layout,parent,false)
        return ViewHolderItem(oneXml)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        val myModel = rResults[position]
        val myYear = myModel?.year
        val myMonth = myModel?.month
        val myDay = myModel?.date
        val nowTime = LocalDate.now()
        var now = 0
        if ((myYear == nowTime.year.toLong()) && (myMonth == nowTime.monthValue.toLong()) && (myDay == nowTime.dayOfMonth.toLong())){
            now = 1
        }else if (myYear == 0L){
            now = 0
        }else if (myYear != null && myMonth != null && myDay != null) {
            if (myYear < nowTime.year.toLong()){
                now = 2
            }else if (myYear == nowTime.year.toLong()){
                if (myMonth < nowTime.monthValue.toLong()){
                    now = 2
                }else if (myMonth == nowTime.monthValue.toLong()){
                    if (myDay < nowTime.dayOfMonth){
                        now = 2
                    }
                }
            }
        }
        holder.oneTextViewTarget.text = myModel?.target.toString()
        holder.detailOne.text = myModel?.priority.toString()
        holder.detailTwo.text = myModel?.importance.toString()
        if (myModel?.taskFinished == 1L){
            holder.oneCheckBox.isChecked = true
        }
        val timeText = myModel?.year.toString() + "/" + myModel?.month.toString() + "/" + myModel?.date.toString()
        if (myModel?.year == 0L){
            val locale = Locale.getDefault()
            if (locale == Locale.JAPAN) {
                // 日本語環境
                holder.timeTextView.text = "指定なし"
            } else {
                // その他の言語環境
                holder.timeTextView.text = "Unspecified"
            }
        }else{
            holder.timeTextView.text = timeText
        }

        realm = Realm.getDefaultInstance()

        /*if (myModel?.taskFinished == 1L){
            holder.oneTextViewTarget.paint.flags = holder.oneTextViewTarget.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }*/
        val sampleModel = realm.where<SampleModel>().equalTo("id",1L).findFirst()
        holder.cardView.setBackgroundColor(
            if (sampleModel?.layoutID == 0L){
                if (myModel?.taskFinished == 1L) {
                    Color.parseColor("#69daf6")
                }else if (now == 1) {
                    Color.parseColor("#fcff48")
                }else if (now == 2) {
                    Color.parseColor("#ff6982")
                }else {Color.WHITE}
            }else if (sampleModel?.layoutID == 1L){
                if (myModel?.taskFinished == 1L) {
                    Color.parseColor("#69daf6")
                }else{
                    if (myModel?.difficultyLevel!! >= 9L){
                        Color.parseColor("#EC407A")
                    }else if (myModel.difficultyLevel >=7L){
                        Color.parseColor("#F06292")
                    }else if (myModel.difficultyLevel >= 5L){
                        Color.parseColor("#F48FB1")
                    }else if (myModel.difficultyLevel >= 3L){
                        Color.parseColor("#F8BBD0")
                    }else{
                        Color.WHITE
                    }
                }
            }else if (sampleModel?.layoutID == 2L){
                if (myModel?.taskFinished == 1L) {
                    Color.parseColor("#69daf6")
                }else{
                    if (myModel?.priority == 5L){
                        Color.parseColor("#EC407A")
                    }else if (myModel?.priority == 4L){
                        Color.parseColor("#F06292")
                    }else if (myModel?.priority == 3L){
                        Color.parseColor("#F48FB1")
                    }else if (myModel?.priority == 2L){
                        Color.parseColor("#F8BBD0")
                    }else{
                        Color.WHITE
                    }
                }
            }else if (sampleModel?.layoutID == 3L){
                if (myModel?.taskFinished == 1L) {
                    Color.parseColor("#69daf6")
                }else{
                    if (myModel?.importance == 5L){
                        Color.parseColor("#EC407A")
                    }else if (myModel?.importance == 4L){
                        Color.parseColor("#F06292")
                    }else if (myModel?.importance == 3L){
                        Color.parseColor("#F48FB1")
                    }else if (myModel?.importance == 2L){
                        Color.parseColor("#F8BBD0")
                    }else{
                        Color.WHITE
                    }
                }
            }else if (sampleModel?.layoutID == 4L){
                if (myModel?.taskFinished == 1L) {
                    Color.parseColor("#69daf6")
                }else{
                    if (myModel?.difficultyLevel!! >= 9L){
                        Color.parseColor("#EC407A")
                    }else if (myModel.difficultyLevel >=7L){
                        Color.parseColor("#F06292")
                    }else if (myModel.difficultyLevel >= 5L){
                        Color.parseColor("#F48FB1")
                    }else if (myModel.difficultyLevel >= 3L){
                        Color.parseColor("#F8BBD0")
                    }else{
                        Color.WHITE
                    }
                }
            }else{
                Color.parseColor("#ffffff")
            })

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, EditActivity::class.java)
            intent.putExtra("ID",myModel?.id)
            it.context.startActivity(intent)
        }

        holder.moreBtn.setOnClickListener{
            val popupMenu = PopupMenu(it.context, it)
            val locale = Locale.getDefault()
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_delete -> {
                        calenderId = myModel?.id!!
                        cancelSchedule(it.context)
                        realm.executeTransaction {
                            myModel.deleteFromRealm()
                        }
                        notifyItemRemoved(position)
                        if (locale == Locale.JAPAN){
                            Toast.makeText(it.context,"削除しました", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(it.context,"Deleted", Toast.LENGTH_SHORT).show()
                        }
                        Handler().postDelayed({
                            notifyDataSetChanged()
                        },500)
                        true
                    }
                    R.id.menu_edit -> {
                        val intent = Intent(it.context,EditActivity::class.java)
                        intent.putExtra("ID",myModel?.id)
                        it.context.startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.rv_menu)
            popupMenu.show()
        }

        holder.oneCheckBox.setOnClickListener{
            val locale = Locale.getDefault()
            val finishId = myModel?.taskFinished
            if (finishId == 1L){
                realm.executeTransaction {
                    myModel.taskFinished = 0L
                }
                holder.cardView.setBackgroundColor(
                    if (sampleModel?.layoutID == 0L){
                        if (myModel?.taskFinished == 1L) {
                            Color.parseColor("#69daf6")
                        }else if (now == 1) {
                            Color.parseColor("#fcff48")
                        }else if (now == 2) {
                            Color.parseColor("#ff6982")
                        }else {Color.WHITE}
                    }else if (sampleModel?.layoutID == 1L){
                        if (myModel?.taskFinished == 1L) {
                            Color.parseColor("#69daf6")
                        }else{
                            if (myModel?.difficultyLevel!! >= 9L){
                                Color.parseColor("#EC407A")
                            }else if (myModel.difficultyLevel >=7L){
                                Color.parseColor("#F06292")
                            }else if (myModel.difficultyLevel >= 5L){
                                Color.parseColor("#F48FB1")
                            }else if (myModel.difficultyLevel >= 3L){
                                Color.parseColor("#F8BBD0")
                            }else{
                                Color.WHITE
                            }
                        }
                    }else if (sampleModel?.layoutID == 2L){
                        if (myModel?.taskFinished == 1L) {
                            Color.parseColor("#69daf6")
                        }else{
                            if (myModel?.priority == 5L){
                                Color.parseColor("#EC407A")
                            }else if (myModel?.priority == 4L){
                                Color.parseColor("#F06292")
                            }else if (myModel?.priority == 3L){
                                Color.parseColor("#F48FB1")
                            }else if (myModel?.priority == 2L){
                                Color.parseColor("#F8BBD0")
                            }else{
                                Color.WHITE
                            }
                        }
                    }else if (sampleModel?.layoutID == 3L){
                        if (myModel?.taskFinished == 1L) {
                            Color.parseColor("#69daf6")
                        }else{
                            if (myModel?.importance == 5L){
                                Color.parseColor("#EC407A")
                            }else if (myModel?.importance == 4L){
                                Color.parseColor("#F06292")
                            }else if (myModel?.importance == 3L){
                                Color.parseColor("#F48FB1")
                            }else if (myModel?.importance == 2L){
                                Color.parseColor("#F8BBD0")
                            }else{
                                Color.WHITE
                            }
                        }
                    }else if (sampleModel?.layoutID == 4L){
                        if (myModel?.taskFinished == 1L) {
                            Color.parseColor("#69daf6")
                        }else{
                            if (myModel?.difficultyLevel!! >= 9L){
                                Color.parseColor("#EC407A")
                            }else if (myModel.difficultyLevel >=7L){
                                Color.parseColor("#F06292")
                            }else if (myModel.difficultyLevel >= 5L){
                                Color.parseColor("#F48FB1")
                            }else if (myModel.difficultyLevel >= 3L){
                                Color.parseColor("#F8BBD0")
                            }else{
                                Color.WHITE
                            }
                        }
                    }else{
                        Color.parseColor("#ffffff")
                    }
                )
                if (myModel.year != 0L){
                    taskText = myModel.target
                    taskYear = myModel.year
                    taskMonth = myModel.month
                    taskDay = myModel.date
                    scheduleNotification(it.context)
                }
                if (locale == Locale.JAPAN) {
                    // 日本語環境
                    Toast.makeText(it.context,"タスクを未完了にしました",Toast.LENGTH_SHORT).show()
                } else {
                    // その他の言語環境
                    Toast.makeText(it.context,"Unfinished the task",Toast.LENGTH_SHORT).show()
                }
            }else{
                realm.executeTransaction {
                    myModel?.taskFinished = 1L
                }
                holder.cardView.setBackgroundColor(Color.parseColor("#69daf6"))
                calenderId = myModel?.id!!
                cancelSchedule(it.context)
                if (locale == Locale.JAPAN) {
                    // 日本語環境
                    Toast.makeText(it.context,"タスクを完了しました",Toast.LENGTH_SHORT).show()
                } else {
                    // その他の言語環境
                    Toast.makeText(it.context,"finished the task",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cancelSchedule(context: Context) {
        /*val getId = intent.getLongExtra("ID",0L)
        val myTaskModelResult = realm.where<MyTaskModel>()
            .equalTo("id",getId).findFirst()*/
        val sIntent = Intent(context, AlarmReceiver::class.java)
        val nID = calenderId.toInt()
        val pending = PendingIntent.getBroadcast(
            context,
            nID,
            sIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val am = ContextCompat.getSystemService(context, AlarmManager::class.java)
        am?.cancel(pending)

    }

    private fun scheduleNotification(context: Context) {
        val scheduleIntent = Intent(context, AlarmReceiver::class.java)
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
        scheduleIntent.putExtra("messageExtra",taskText)
        scheduleIntent.putExtra("notificationId", nfID)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            nfID,
            scheduleIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
        val sTime = getTime()
        alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,sTime,pendingIntent)
    }

    private fun getTime():Long {
        val sCal = Calendar.getInstance()
        sCal.set(taskYear.toInt(),(taskMonth-1).toInt(),taskDay.toInt(),7,0)
        return sCal.timeInMillis
    }

    override fun getItemCount(): Int {
        return rResults.size
    }
}