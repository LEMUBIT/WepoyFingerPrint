package com.lemuel.lemubit.fingerprinttest

import android.app.Application

import com.bugsnag.android.Bugsnag
import com.wepoy.fp.Bione
import com.wepoy.fp.FingerprintImage
import com.wepoy.fp.FingerprintScanner
import com.wepoy.util.Result

//Class to get finger print data
object Fingerprint {
    fun getFingerPrint(application: Application, enrolView: EnrolView): FingerPrintResponse {
        val mScanner: FingerprintScanner = FingerprintScanner.getInstance(application.applicationContext)
        var fi: FingerprintImage
        var res: Result?
        var fingerPrintCount = 0
        var x = 0
        var state: Int
        enrolView.executionDone(false)
        do {
            x++
            enrolView.showInfoToast(x.toString() + " times")
            enrolView.showProgressDialog(application.getString(R.string.loading), application.getString(R.string.press_finger))

            mScanner.prepare()
            do {
                res = mScanner.capture()
            } while (res!!.error == FingerprintScanner.NO_FINGER)


            if (res.error != FingerprintScanner.RESULT_OK) {
                enrolView.showInfoToast(application.getString(R.string.capture_image_failed))
                Bugsnag.notify(Exception())
            }

            fi = res.data as FingerprintImage

            enrolView.showProgressDialog(application.getString(R.string.loading), application.getString(R.string.enrolling))
            Bugsnag.leaveBreadcrumb(application.getString(R.string.enrolling))

            res = Bione.extractFeature(fi)

            if (res.error != Bione.RESULT_OK) {
                enrolView.showInfoToast(application.getString(R.string.enroll_failed_because_of_extract_feature))
                Bugsnag.leaveBreadcrumb(application.getString(R.string.enroll_failed_because_of_extract_feature))
                Bugsnag.notify(Exception())
                state = 0
            } else {
                fingerPrintCount++
                enrolView.showInfoToast("FingerPrint " + (fingerPrintCount + 1))
                state = 1
            }

            mScanner.finish()
        } while (false)

        enrolView.dismissProgressDialog()

        //todo check, only to be done if crash, if not next method should be called
        enrolView.executionDone(true)
        return FingerPrintResponse(res, state)
    }

    fun saveFingerPrint(application: Application, result: Result, enrolView: EnrolView) {
        val fi: FingerprintImage? = null
        var fpFeat: ByteArray? = null
        var fpTemp: ByteArray? = null
        var res = result
        var fingerprintGood: Boolean? = null

        do {
            val x = 0
            //Extract byte data gotten from Bione Feature and make Template
            fpFeat = res!!.data as ByteArray
            res = Bione.makeTemplate(fpFeat, fpFeat, fpFeat)

            if (res!!.error != Bione.RESULT_OK) {
                enrolView.showInfoToast(application.getString(R.string.enroll_failed_because_of_make_template))
                fingerprintGood = false
                Bugsnag.leaveBreadcrumb(application.getString(R.string.enroll_failed_because_of_make_template))
                Bugsnag.notify(Exception())
                break
            }

            fpTemp = res!!.data as ByteArray

            val id = Bione.getFreeID()
            if (id < 0) {
                enrolView.showInfoToast(application.getString(R.string.enroll_failed_because_of_get_id))
                fingerprintGood = false
                break
            }
            val ret = Bione.enroll(id, fpTemp)

            if (ret != Bione.RESULT_OK) {
                enrolView.showInfoToast(application.getString(R.string.enroll_failed_because_of_error))
                fingerprintGood = false
                break
            }

            enrolView.showInfoToast(application.getString(R.string.enroll_success) + id)
            fingerprintGood = true

        } while (false)

        enrolView.dismissProgressDialog()
        enrolView.executionDone(true)

    }

}
