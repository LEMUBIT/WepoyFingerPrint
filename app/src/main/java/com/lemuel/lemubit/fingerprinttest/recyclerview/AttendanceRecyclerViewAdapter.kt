package com.lemuel.lemubit.fingerprinttest.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.model.AttendanceRealmModel
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

class AttendanceRecyclerViewAdapter internal constructor(data: RealmResults<AttendanceRealmModel>) : RealmRecyclerViewAdapter<AttendanceRealmModel, AttendanceRecyclerViewAdapter.MyViewHolder>(data, true) {

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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.attendance_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val attendanceRecord = getItem(position)
        holder.employeeName.text = attendanceRecord!!.name
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

