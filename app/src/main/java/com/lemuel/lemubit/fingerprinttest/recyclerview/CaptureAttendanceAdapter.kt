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

class CaptureAttendanceAdapter internal constructor(data: RealmResults<AttendanceRealmModel>, val context: Context) : RealmRecyclerViewAdapter<AttendanceRealmModel, CaptureAttendanceAdapter.MyViewHolder>(data, true) {

    init {
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        setHasStableIds(true)
    }

    internal fun newRecord() {
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.attendance_capture_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val attendanceRecord = getItem(position)
        holder.employeeName.text = attendanceRecord!!.name
        if (DateAndTime.isPassedDeadline(attendanceRecord.time))
            holder.signInTime.setTextColor(context.resources.getColor(R.color.afterTime))
        holder.signInTime.text = attendanceRecord.time

    }

    override fun getItemId(index: Int): Long {

        return getItem(index)!!.id.toLong()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var employeeName: TextView
        var signInTime: TextView

        init {
            employeeName = view.findViewById(R.id.txt_name)
            signInTime = view.findViewById(R.id.txt_time)
        }
    }
}

