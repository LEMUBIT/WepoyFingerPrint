package com.lemuel.lemubit.fingerprinttest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.wepoy.fp.Bione;
import com.wepoy.fp.FingerprintImage;
import com.wepoy.fp.FingerprintScanner;
import com.wepoy.util.Result;

class Util {

    public static void updateFingerprintImage(MainActivity mainActivity, FingerprintImage fi) {
        byte[] fpBmp = null;
        Bitmap bitmap;
        if (fi == null || (fpBmp = fi.convert2Bmp()) == null || (bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length)) == null) {
            bitmap = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.nofinger);
        }
        mainActivity.mHandler.sendMessage(mainActivity.mHandler.obtainMessage(MainActivity.MSG_UPDATE_IMAGE, bitmap));
    }

    static void closeDevice(final EnrolActivity enrolActivity, final boolean finish) {
        new Thread() {
            @Override
            public void run() {
                enrolActivity.showProgressDialog(enrolActivity.getString(R.string.loading), enrolActivity.getString(R.string.closing_device));
                int error;
                //todo check if Rxjava task is running


                if ((error = enrolActivity.getMScanner().close()) != FingerprintScanner.RESULT_OK) {
                    enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_close_failed));
                } else {
                    enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_close_success));
                }
                if ((error = enrolActivity.getMScanner().powerOff()) != FingerprintScanner.RESULT_OK) {
                    enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_power_off_failed));
                }
                if ((error = Bione.exit()) != Bione.RESULT_OK) {
                    enrolActivity.showInfoToast(enrolActivity.getString(R.string.algorithm_cleanup_failed));
                }
                if (finish) {
                    enrolActivity.finishActivity();
                }
                enrolActivity.dismissProgressDialog();
            }
        }.start();
    }

    static void openDevice(final EnrolActivity enrolActivity) {

        new Thread() {
            @Override
            public void run() {
                enrolActivity.showProgressDialog(enrolActivity.getString(R.string.loading), enrolActivity.getString(R.string.preparing_device));
                int error;
                if ((error = enrolActivity.getMScanner().powerOn()) != FingerprintScanner.RESULT_OK) {
                    enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_power_on_failed));
                }
                if ((error = enrolActivity.getMScanner().open()) != FingerprintScanner.RESULT_OK) {
                    enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_open_failed));
                } else {
                    Result res = enrolActivity.getMScanner().getSN();
                    enrolActivity.showInfoToast(enrolActivity.getString(R.string.fingerprint_device_open_success));
                }
                if ((error = Bione.initialize(enrolActivity, EnrolActivity.Companion.getFP_DB_PATH())) != Bione.RESULT_OK) {
                    enrolActivity.showInfoToast(enrolActivity.getString(R.string.algorithm_initialization_failed));
                }
                Log.i(enrolActivity.Companion.getTAG(), "Fingerprint algorithm version: " + Bione.getVersion());
                enrolActivity.dismissProgressDialog();
            }
        }.start();
    }
}
