package com.lemuel.lemubit.fingerprinttest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.wepoy.fp.Bione;
import com.wepoy.fp.FingerprintImage;
import com.wepoy.fp.FingerprintScanner;
import com.wepoy.util.Result;

class Util {
    public static String getFingerprintErrorString(MainActivity mainActivity, int error) {
        int strid;
        switch (error) {
            case FingerprintScanner.RESULT_OK:
                strid = R.string.operation_successful;
                break;
            case FingerprintScanner.RESULT_FAIL:
                strid = R.string.error_operation_failed;
                break;
            case FingerprintScanner.WRONG_CONNECTION:
                strid = R.string.error_wrong_connection;
                break;
            case FingerprintScanner.DEVICE_BUSY:
                strid = R.string.error_device_busy;
                break;
            case FingerprintScanner.DEVICE_NOT_OPEN:
                strid = R.string.error_device_not_open;
                break;
            case FingerprintScanner.TIMEOUT:
                strid = R.string.error_timeout;
                break;
            case FingerprintScanner.NO_PERMISSION:
                strid = R.string.error_no_permission;
                break;
            case FingerprintScanner.WRONG_PARAMETER:
                strid = R.string.error_wrong_parameter;
                break;
            case FingerprintScanner.DECODE_ERROR:
                strid = R.string.error_decode;
                break;
            case FingerprintScanner.INIT_FAIL:
                strid = R.string.error_initialization_failed;
                break;
            case FingerprintScanner.UNKNOWN_ERROR:
                strid = R.string.error_unknown;
                break;
            case FingerprintScanner.NOT_SUPPORT:
                strid = R.string.error_not_support;
                break;
            case FingerprintScanner.NOT_ENOUGH_MEMORY:
                strid = R.string.error_not_enough_memory;
                break;
            case FingerprintScanner.DEVICE_NOT_FOUND:
                strid = R.string.error_device_not_found;
                break;
            case FingerprintScanner.DEVICE_REOPEN:
                strid = R.string.error_device_reopen;
                break;
            case FingerprintScanner.NO_FINGER:
                strid = R.string.error_no_finger;
                break;
            case Bione.INITIALIZE_ERROR:
                strid = R.string.error_algorithm_initialization_failed;
                break;
            case Bione.INVALID_FEATURE_DATA:
                strid = R.string.error_invalid_feature_data;
                break;
            case Bione.BAD_IMAGE:
                strid = R.string.error_bad_image;
                break;
            case Bione.NOT_MATCH:
                strid = R.string.error_not_match;
                break;
            case Bione.LOW_POINT:
                strid = R.string.error_low_point;
                break;
            case Bione.NO_RESULT:
                strid = R.string.error_no_result;
                break;
            case Bione.OUT_OF_BOUND:
                strid = R.string.error_out_of_bound;
                break;
            case Bione.DATABASE_FULL:
                strid = R.string.error_database_full;
                break;
            case Bione.LIBRARY_MISSING:
                strid = R.string.error_library_missing;
                break;
            case Bione.UNINITIALIZE:
                strid = R.string.error_algorithm_uninitialize;
                break;
            case Bione.REINITIALIZE:
                strid = R.string.error_algorithm_reinitialize;
                break;
            case Bione.REPEATED_ENROLL:
                strid = R.string.error_repeated_enroll;
                break;
            case Bione.NOT_ENROLLED:
                strid = R.string.error_not_enrolled;
                break;
            default:
                strid = R.string.error_other;
                break;
        }
        return mainActivity.getString(strid);
    }

    public static void updateFingerprintImage(MainActivity mainActivity, FingerprintImage fi) {
        byte[] fpBmp = null;
        Bitmap bitmap;
        if (fi == null || (fpBmp = fi.convert2Bmp()) == null || (bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length)) == null) {
            bitmap = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.nofinger);
        }
        mainActivity.mHandler.sendMessage(mainActivity.mHandler.obtainMessage(MainActivity.MSG_UPDATE_IMAGE, bitmap));
    }

    static void closeDevice(final MainActivity mainActivity, final boolean finish) {
        new Thread() {
            @Override
            public void run() {
                mainActivity.showProgressDialog(mainActivity.getString(R.string.loading), mainActivity.getString(R.string.closing_device));
                mainActivity.enableControl(false);
                int error;
                if (mainActivity.mTask != null && mainActivity.mTask.getStatus() != AsyncTask.Status.FINISHED) {
                    mainActivity.mTask.cancel(false);
                    mainActivity.mTask.waitForDone();
                }
                if ((error = mainActivity.mScanner.close()) != FingerprintScanner.RESULT_OK) {
                    mainActivity.showErrorDialog(mainActivity.getString(R.string.fingerprint_device_close_failed), getFingerprintErrorString(mainActivity, error));
                } else {
                    mainActivity.showInfoToast(mainActivity.getString(R.string.fingerprint_device_close_success));
                }
                if ((error = mainActivity.mScanner.powerOff()) != FingerprintScanner.RESULT_OK) {
                    mainActivity.showErrorDialog(mainActivity.getString(R.string.fingerprint_device_power_off_failed), getFingerprintErrorString(mainActivity, error));
                }
                if ((error = Bione.exit()) != Bione.RESULT_OK) {
                    mainActivity.showErrorDialog(mainActivity.getString(R.string.algorithm_cleanup_failed), getFingerprintErrorString(mainActivity, error));
                }
                if (finish) {
                    mainActivity.finishActivity();
                }
                mainActivity.dismissProgressDialog();
            }
        }.start();
    }
}
