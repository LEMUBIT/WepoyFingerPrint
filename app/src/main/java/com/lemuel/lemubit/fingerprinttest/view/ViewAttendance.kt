package com.lemuel.lemubit.fingerprinttest.view

import android.app.DatePickerDialog
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
import kotlinx.android.synthetic.main.activity_view_attendance.*
import java.text.SimpleDateFormat
import java.util.*

class ViewAttendance : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private var recyclerView: RecyclerView? = null
    private var adapter: ViewAttendanceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_attendance)
        recyclerView = findViewById(R.id.recyclerv_record)

        setUpRecyclerView()

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        btn_filter.setOnClickListener { DatePickerDialog(this@ViewAttendance, this, year, month, day).show() }

        txt_filterdate.setOnClickListener {
            adapter = ViewAttendanceAdapter(DataHelper.getAttendanceRecord(), this)
            recyclerView?.adapter = adapter
            adapter!!.newRecord()
        }

    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {

        val format = formatDate(p1, p2, p3)
        adapter = ViewAttendanceAdapter(DataHelper.getAttendanceRecord(format), this)
        recyclerView?.adapter = adapter
        adapter!!.newRecord()
        Toast.makeText(this@ViewAttendance, format, Toast.LENGTH_SHORT).show()
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val cal = Calendar.getInstance()
        cal.set(year, month, day)
        val format1 = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
        val formatted = format1.format(cal.time)

        return formatted
    }

    private fun setUpRecyclerView() {
        adapter = ViewAttendanceAdapter(DataHelper.getAttendanceRecord(), this)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
        recyclerView?.setHasFixedSize(true)
        recyclerView?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }


}
