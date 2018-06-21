package com.lemuel.lemubit.fingerprinttest.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bugsnag.android.Bugsnag
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.presenter.HomeMenuPresenter
import com.lemuel.lemubit.fingerprinttest.view.EnrolActivity
import com.lemuel.lemubit.fingerprinttest.viewInterface.HomeMenuView
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_home_menu.*


class HomeMenu : AppCompatActivity(), HomeMenuView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_menu)

        // Initialize Realm (just once per application)
        Realm.init(applicationContext)

        //Initialize Bugsnag to track crash
        Bugsnag.init(this)

        var homeMenuPresenter=HomeMenuPresenter(this,this)

        btn_enrol.setOnClickListener {
            startActivity(Intent(this, EnrolActivity::class.java))
        }

        btn_takeAttendance.setOnClickListener {
            startActivity(Intent(this, TakeAttendance::class.java))
        }

        btn_clearAttendance.setOnClickListener {
            homeMenuPresenter.clearDatabase()
        }

    }

    override fun onDatabaseCleared(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
