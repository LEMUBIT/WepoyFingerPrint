package com.lemuel.lemubit.fingerprinttest.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.helper.DateAndTime
import com.lemuel.lemubit.fingerprinttest.model.AttendanceRealmModel
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

class ViewAttendanceAdapter internal constructor(data: RealmResults<AttendanceRealmModel>, val context: Context) : RealmRecyclerViewAdapter<AttendanceRealmModel, ViewAttendanceAdapter.MyViewHolder>(data, true) {

    init {
        setHasStableIds(true)
    }

    internal fun newRecord() {
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.attendance_view_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val attendanceRecord = getItem(position)

        holder.employeeName.text = attendanceRecord!!.name

        holder.employeeSurName.text = attendanceRecord.lastName

        if (DateAndTime.isPassedDeadline(attendanceRecord.time))
            holder.signInTime.setTextColor(context.resources.getColor(R.color.afterTime))
        holder.signInTime.text = attendanceRecord.time

        holder.signInDate.text = attendanceRecord.date

    }

    override fun getItemId(index: Int): Long {

        return getItem(index)!!.id.toLong()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var employeeName: TextView
        var employeeSurName: TextView
        var signInTime: TextView
        var signInDate: TextView

        init {
            employeeName = view.findViewById(R.id.txt_record_name)
            employeeSurName = view.findViewById(R.id.txt_record_surname)
            signInTime = view.findViewById(R.id.txt_record_time)
            signInDate = view.findViewById(R.id.txt_record_date)
        }
    }
}
