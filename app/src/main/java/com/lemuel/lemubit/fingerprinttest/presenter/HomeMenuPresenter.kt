package com.lemuel.lemubit.fingerprinttest.presenter

import android.app.Activity

import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.helper.ErrorStrings
import com.lemuel.lemubit.fingerprinttest.view.HomeMenu
import com.lemuel.lemubit.fingerprinttest.viewInterface.HomeMenuView
import com.wepoy.fp.Bione
import com.wepoy.fp.FingerprintScanner

class HomeMenuPresenter(internal var activity: Activity, private var homeMenuView: HomeMenuView) {


    var mScanner: FingerprintScanner = FingerprintScanner.getInstance(activity.applicationContext)

    fun clearDatabase() {
        val error = Bione.clear()

        if (error == Bione.RESULT_OK) {
            homeMenuView.onDatabaseCleared(activity.getString(R.string.clear_fingerprint_database_success))
        } else {
            homeMenuView.onDatabaseCleared(ErrorStrings.getFingerprintErrorString(activity, error))
        }

    }


    fun openDevice() = object : Thread() {
        override fun run() {
            homeMenuView.showToastInfo(activity.getString(R.string.preparing_device))

            if ((mScanner.powerOn()) != FingerprintScanner.RESULT_OK)   homeMenuView.showToastInfo(activity.getString(R.string.fingerprint_device_power_on_failed))

            if ((mScanner.open()) != FingerprintScanner.RESULT_OK)   homeMenuView.showToastInfo(activity.getString(R.string.fingerprint_device_open_failed))
            else   homeMenuView.showToastInfo(activity.getString(R.string.fingerprint_device_open_success))

            if (( Bione.initialize(activity, HomeMenu.FP_DB_PATH)) != Bione.RESULT_OK)   homeMenuView.showToastInfo(activity.getString(R.string.algorithm_initialization_failed))
        }
    }.start()

    fun closeDevice() = object : Thread() {
        override fun run() {
            homeMenuView.showToastInfo(activity.getString(R.string.closing_device))

            if ((mScanner.close()) != FingerprintScanner.RESULT_OK)
                homeMenuView.showToastInfo(activity.getString(R.string.fingerprint_device_close_failed))
            else homeMenuView.showToastInfo(activity.getString(R.string.fingerprint_device_close_success))

            if (( mScanner.powerOff()) != FingerprintScanner.RESULT_OK) homeMenuView.showToastInfo(activity.getString(R.string.fingerprint_device_power_off_failed))

            if ((Bione.exit()) != Bione.RESULT_OK) homeMenuView.showToastInfo(activity.getString(R.string.algorithm_cleanup_failed))
        }
    }.start()


}
