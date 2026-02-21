package com.ruriboshi.taskpriority

import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolderItem(v:View):RecyclerView.ViewHolder(v) {
    var oneTextViewTarget:TextView = v.findViewById(R.id.oneTaskTextView)
    var detailOne:TextView = v.findViewById(R.id.detailOne)
    var detailTwo:TextView = v.findViewById(R.id.detailTwo)
    var timeTextView:TextView = v.findViewById(R.id.timeText)
    var cardView:LinearLayout = v.findViewById(R.id.layout)
    var moreBtn:ImageButton = v.findViewById(R.id.imageButtonMenu)
    var oneCheckBox:CheckBox = v.findViewById(R.id.oneCheckBox)
}