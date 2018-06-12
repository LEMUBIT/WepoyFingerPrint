package com.lemuel.lemubit.fingerprinttest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bugsnag.android.Bugsnag
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

    }

}
