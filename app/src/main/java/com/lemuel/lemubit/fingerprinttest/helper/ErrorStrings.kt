package com.lemuel.lemubit.fingerprinttest.helper

import android.app.Activity

import com.lemuel.lemubit.fingerprinttest.R
import com.wepoy.fp.Bione
import com.wepoy.fp.FingerprintScanner

object ErrorStrings {
    fun getFingerprintErrorString(activity: Activity, error: Int): String {
        val errorString: Int
        when (error) {
            FingerprintScanner.RESULT_OK -> errorString = R.string.operation_successful
            FingerprintScanner.RESULT_FAIL -> errorString = R.string.error_operation_failed
            FingerprintScanner.WRONG_CONNECTION -> errorString = R.string.error_wrong_connection
            FingerprintScanner.DEVICE_BUSY -> errorString = R.string.error_device_busy
            FingerprintScanner.DEVICE_NOT_OPEN -> errorString = R.string.error_device_not_open
            FingerprintScanner.TIMEOUT -> errorString = R.string.error_timeout
            FingerprintScanner.NO_PERMISSION -> errorString = R.string.error_no_permission
            FingerprintScanner.WRONG_PARAMETER -> errorString = R.string.error_wrong_parameter
            FingerprintScanner.DECODE_ERROR -> errorString = R.string.error_decode
            FingerprintScanner.INIT_FAIL -> errorString = R.string.error_initialization_failed
            FingerprintScanner.UNKNOWN_ERROR -> errorString = R.string.error_unknown
            FingerprintScanner.NOT_SUPPORT -> errorString = R.string.error_not_support
            FingerprintScanner.NOT_ENOUGH_MEMORY -> errorString = R.string.error_not_enough_memory
            FingerprintScanner.DEVICE_NOT_FOUND -> errorString = R.string.error_device_not_found
            FingerprintScanner.DEVICE_REOPEN -> errorString = R.string.error_device_reopen
            FingerprintScanner.NO_FINGER -> errorString = R.string.error_no_finger
            Bione.INITIALIZE_ERROR -> errorString = R.string.error_algorithm_initialization_failed
            Bione.INVALID_FEATURE_DATA -> errorString = R.string.error_invalid_feature_data
            Bione.BAD_IMAGE -> errorString = R.string.error_bad_image
            Bione.NOT_MATCH -> errorString = R.string.error_not_match
            Bione.LOW_POINT -> errorString = R.string.error_low_point
            Bione.NO_RESULT -> errorString = R.string.error_no_result
            Bione.OUT_OF_BOUND -> errorString = R.string.error_out_of_bound
            Bione.DATABASE_FULL -> errorString = R.string.error_database_full
            Bione.LIBRARY_MISSING -> errorString = R.string.error_library_missing
            Bione.UNINITIALIZE -> errorString = R.string.error_algorithm_uninitialize
            Bione.REINITIALIZE -> errorString = R.string.error_algorithm_reinitialize
            Bione.REPEATED_ENROLL -> errorString = R.string.error_repeated_enroll
            Bione.NOT_ENROLLED -> errorString = R.string.error_not_enrolled
            else -> errorString = R.string.error_other
        }
        return activity.getString(errorString)
    }
}
