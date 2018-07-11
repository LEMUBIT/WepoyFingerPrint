package com.lemuel.lemubit.fingerprinttest.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.Toast
import com.bugsnag.android.Bugsnag
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.presenter.HomeMenuPresenter
import com.lemuel.lemubit.fingerprinttest.viewInterface.HomeMenuView
import com.rollbar.android.Rollbar
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_home_menu.*


class HomeMenu : AppCompatActivity(), HomeMenuView {
    lateinit var homeMenuPresenter: HomeMenuPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_menu)

        // Initialize Realm (just once per application)
        Realm.init(applicationContext)

        //Initialize Bugsnag to track crash
        Bugsnag.init(this)

        Rollbar.init(this)
        homeMenuPresenter = HomeMenuPresenter(this, this)

        btn_enrol.setOnClickListener {
            startActivity(Intent(this, EnrolActivity::class.java))
        }

        btn_takeAttendance.setOnClickListener {
            startActivity(Intent(this, EnrolActivity::class.java))
        }

        btn_clearAttendance.setOnClickListener {
            homeMenuPresenter.clearDatabase()
        }

        btn_viewAttendance.setOnClickListener {
            startActivity(Intent(this, ViewAttendance::class.java))
        }
        //!todo :Used to test, delete it!... Use same image to get fingerPrint in CaptureAttendance
        animation_view.setOnClickListener {
            Toast.makeText(this, "Ive been Clicked", Toast.LENGTH_LONG).show()
        }

    }

    override fun onResume() {
        super.onResume()
        homeMenuPresenter.openDevice()
    }

    override fun onDestroy() {
        super.onDestroy()
        homeMenuPresenter.closeDevice()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                return false
            }
        //todo Check how to stop home button from closing app
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDatabaseCleared(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showToastInfo(message: String?) {
        //   Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        val TAG = "FingerprintDemo"
        val FP_DB_PATH = "/sdcard/fp.db"
    }
}
