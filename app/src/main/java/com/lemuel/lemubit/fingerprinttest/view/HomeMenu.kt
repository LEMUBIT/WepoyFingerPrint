package com.lemuel.lemubit.fingerprinttest.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bugsnag.android.Bugsnag
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.view.EnrolActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_home_menu.*


class HomeMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_menu)

        // Initialize Realm (just once per application)
        Realm.init(applicationContext)

        //Initialize Bugsnag to track crash
        Bugsnag.init(this)

        btn_enrol.setOnClickListener {
            startActivity(Intent(this, EnrolActivity::class.java))
        }

        btn_takeAttendance.setOnClickListener {

        }

    }

}
