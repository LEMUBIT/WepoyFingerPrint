package com.lemuel.lemubit.fingerprinttest;

import android.app.Application;

import com.bugsnag.android.Bugsnag;
import com.wepoy.fp.Bione;
import com.wepoy.fp.FingerprintImage;
import com.wepoy.fp.FingerprintScanner;
import com.wepoy.util.Result;

//Class to get finger print data
public class Fingerprint {
    public static String getFingerPrint(Application application, EnrolView enrolView) {
       FingerprintScanner mScanner;
        FingerprintImage fi;
        Result res;
        int fingerPrintCount = 0;
        int x=0;
        String state;
        mScanner = FingerprintScanner.getInstance(application.getApplicationContext());

        do {

            x++;
            enrolView.showInfoToast(x + " times");
            enrolView.showProgressDialog(application.getString(R.string.loading), application.getString(R.string.press_finger));

            mScanner.prepare();
            do {
                res = mScanner.capture();
            } while (res.error == FingerprintScanner.NO_FINGER);


            if (res.error != FingerprintScanner.RESULT_OK) {
                enrolView.showInfoToast(application.getString(R.string.capture_image_failed));
                Bugsnag.notify(new Exception());
            }

            //get fingerprint image gotten from capture
            fi = (FingerprintImage) res.data;

            //Enrolling
            enrolView.showProgressDialog(application.getString(R.string.loading), application.getString(R.string.enrolling));
            Bugsnag.leaveBreadcrumb(application.getString(R.string.enrolling));


            //Extract feature gotten from Fingerprint image
            res = Bione.extractFeature(fi);

            if (res.error != Bione.RESULT_OK) {
                enrolView.showInfoToast(application.getString(R.string.enroll_failed_because_of_extract_feature));
                //fingerprintGood = false;
                Bugsnag.leaveBreadcrumb(application.getString(R.string.enroll_failed_because_of_extract_feature));
                Bugsnag.notify(new Exception());
                state="bad";
            } else {
                fingerPrintCount++;
                enrolView.showInfoToast("FingerPrint " + (fingerPrintCount + 1));
                state="good";
            }

            mScanner.finish();
        } while (false);

        enrolView.dismissProgressDialog();
        return state;
    }
}
