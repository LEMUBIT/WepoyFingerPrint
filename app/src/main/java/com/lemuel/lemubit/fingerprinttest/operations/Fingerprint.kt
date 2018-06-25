package com.lemuel.lemubit.fingerprinttest.operations

import android.app.Application
import com.bugsnag.android.Bugsnag
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.viewInterface.FingerPrintInterface
import com.wepoy.fp.Bione
import com.wepoy.fp.FingerprintImage
import com.wepoy.fp.FingerprintScanner
import com.wepoy.util.Result

/*Handles the getting and saving of Fingerprint data*/
object Fingerprint {

    /**@Returns the raw result of fingerprint scan**/
    fun getFingerPrint(application: Application, FingerPrintInterface: FingerPrintInterface): Result? {
        val mScanner: FingerprintScanner = FingerprintScanner.getInstance(application.applicationContext)
        var fi: FingerprintImage
        var res: Result?

        do {
            FingerPrintInterface.showProgressDialog(application.getString(R.string.loading), application.getString(R.string.press_finger))

            mScanner.prepare()

            do res = mScanner.capture()
            while (res!!.error == FingerprintScanner.NO_FINGER)


            if (res.error != FingerprintScanner.RESULT_OK) {
                FingerPrintInterface.showInfoToast(application.getString(R.string.capture_image_failed))
            }

            fi = res.data as FingerprintImage

            FingerPrintInterface.showProgressDialog(application.getString(R.string.loading), application.getString(R.string.enrolling))

            Bugsnag.leaveBreadcrumb(application.getString(R.string.enrolling))

            res = Bione.extractFeature(fi)

            if (res.error != Bione.RESULT_OK) {
                FingerPrintInterface.showInfoToast(application.getString(R.string.enroll_failed_because_of_extract_feature))
                res=null
            }

            mScanner.finish()
        } while (false)

        FingerPrintInterface.dismissProgressDialog()

        return res
    }

    /**@Returns the ID of saved fingerprint data**/
    fun saveFingerPrint(application: Application, result: Result, FingerPrintInterface: FingerPrintInterface): Int {
        var fpFeat: ByteArray?
        var fpTemp: ByteArray?
        var res: Result? = result
        var id = 0

        if (result == null)
            id = -1

        do {
            if (res == null) break
            fpFeat = res.data as ByteArray
            res = Bione.makeTemplate(fpFeat, fpFeat, fpFeat)

            if (res.error != Bione.RESULT_OK) {
                FingerPrintInterface.showInfoToast(application.getString(R.string.enroll_failed_because_of_make_template))
                break
            }

            fpTemp = res.data as ByteArray
            id = Bione.getFreeID()

            if (id < 0) {
                FingerPrintInterface.showInfoToast(application.getString(R.string.enroll_failed_because_of_get_id))
                break
            }

            val ret = Bione.enroll(id, fpTemp)

            if (ret != Bione.RESULT_OK) {
                FingerPrintInterface.showInfoToast(application.getString(R.string.enroll_failed_because_of_error))
                break
            }

            FingerPrintInterface.showInfoToast(application.getString(R.string.enroll_success) + id)

        } while (false)

        FingerPrintInterface.dismissProgressDialog()
        return id
    }

    fun getUserID(result: Result): Int? {
        var fpFeat: ByteArray?
        var res: Result? = result
        var userID: Int? = null

        do {
            if (res == null) break
            fpFeat = res.data as ByteArray
            userID = Bione.identify(fpFeat);
        } while (false)

        return userID
    }

}
