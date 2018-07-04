package com.lemuel.lemubit.fingerprinttest.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.DatePicker
import android.widget.Toast
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.model.DataHelper
import com.lemuel.lemubit.fingerprinttest.recyclerview.ViewAttendanceAdapter
import java.text.SimpleDateFormat
import java.util.*

class ViewAttendance : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var adapter: ViewAttendanceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_attendance)
        recyclerView = findViewById(R.id.recyclerv_record)
        val newFragment = TimePickerFragment()

        //todo: Crashing showing non null
//        setUpRecyclerView()
//        newFragment.show(fragmentManager, "datePicker")
//        btn_filter.setOnClickListener {  }
    }

    private fun setUpRecyclerView() {
        adapter = ViewAttendanceAdapter(DataHelper.getAttendanceRecord(), this)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
        recyclerView?.setHasFixedSize(true)
        recyclerView?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }


    class TimePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(activity, this, year, month, day)
            dialog.datePicker.maxDate = c.timeInMillis
            return dialog
        }


        override fun onDateSet(datePicker: DatePicker, i: Int, i1: Int, i2: Int) {

            val cal = Calendar.getInstance()
            cal.set(i, i1, i2)
            val format1 = SimpleDateFormat("dd/MM/yyyy", Locale.UK)

            val formatted = format1.format(cal.time)

            Toast.makeText(activity, formatted, Toast.LENGTH_SHORT).show()
        }
    }

}
