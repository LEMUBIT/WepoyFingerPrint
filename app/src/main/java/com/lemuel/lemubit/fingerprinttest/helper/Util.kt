package com.lemuel.lemubit.fingerprinttest.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.lemuel.lemubit.fingerprinttest.view.EnrolActivity
import com.lemuel.lemubit.fingerprinttest.MainActivity
import com.lemuel.lemubit.fingerprinttest.R
import com.wepoy.fp.Bione
import com.wepoy.fp.FingerprintImage
import com.wepoy.fp.FingerprintScanner
import com.wepoy.util.Result

internal object Util {

    fun closeDevice(enrolActivity: EnrolActivity, finish: Boolean) = object : Thread() {
        override fun run() {
            enrolActivity.showProgressDialog(enrolActivity.getString(R.string.loading), enrolActivity.getString(R.string.closing_device))
            var error: Int
            //todo -check if Rxjava task is running

            if (( enrolActivity.mScanner.close()) != FingerprintScanner.RESULT_OK)
                enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_close_failed))
            else enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_close_success))

            if (( enrolActivity.mScanner.powerOff()) != FingerprintScanner.RESULT_OK) enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_power_off_failed))

            if ((Bione.exit()) != Bione.RESULT_OK) enrolActivity.showInfoToast(enrolActivity.getString(R.string.algorithm_cleanup_failed))

            if (finish) enrolActivity.finishActivity()

            enrolActivity.dismissProgressDialog()
        }
    }.start()

    fun openDevice(enrolActivity: EnrolActivity) = object : Thread() {
        override fun run() {
            enrolActivity.showProgressDialog(enrolActivity.getString(R.string.loading), enrolActivity.getString(R.string.preparing_device))
            var error: Int
            if ((enrolActivity.mScanner.powerOn()) != FingerprintScanner.RESULT_OK) enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_power_on_failed))

            if ((enrolActivity.mScanner.open()) != FingerprintScanner.RESULT_OK) enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_open_failed))
            else enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_open_success))

            if (( Bione.initialize(enrolActivity, EnrolActivity.FP_DB_PATH)) != Bione.RESULT_OK) enrolActivity.showInfoToast(enrolActivity.getString(R.string.algorithm_initialization_failed))

            enrolActivity.dismissProgressDialog()
        }
    }.start()
}
