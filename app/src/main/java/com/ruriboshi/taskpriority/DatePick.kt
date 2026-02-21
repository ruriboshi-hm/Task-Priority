package com.ruriboshi.taskpriority

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment

class DatePick:DialogFragment(),DatePickerDialog.OnDateSetListener {

    var myYear = 0
    var myMonth = 0
    var myDay = 0

    override fun onCreateDialog(savedInstanceState: Bundle?):Dialog{
        /*val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)*/
        val year = myYear
        val month = myMonth
        val day = myDay

        return DatePickerDialog(requireContext(),activity as EditActivity,year,month,day)

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {

    }
}