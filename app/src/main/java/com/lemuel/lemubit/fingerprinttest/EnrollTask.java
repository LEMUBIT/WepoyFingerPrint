package com.lemuel.lemubit.fingerprinttest;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.balsikandar.crashreporter.CrashReporter;
import com.wepoy.fp.Bione;
import com.wepoy.fp.FingerprintImage;
import com.wepoy.fp.FingerprintScanner;
import com.wepoy.util.Result;

import io.realm.Realm;

class EnrollTask extends AsyncTask<String, Integer, Void> {
    private EnrolActivity enrolActivity;
    private boolean mIsDone = false;

    public EnrollTask(EnrolActivity enrolActivity) {
        this.enrolActivity = enrolActivity;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... params) {

        FingerprintImage fi = null;
        byte[] fpFeat = null, fpTemp = null;
        Result res;
        enrolActivity.choice = params[0];
        do {
            if (params[0].equals("enroll")) {
                enrolActivity.showProgressDialog(enrolActivity.getString(R.string.loading), enrolActivity.getString(R.string.press_finger));
                enrolActivity.mScanner.prepare();
                do {
                    res = enrolActivity.mScanner.capture();
                } while (res.error == FingerprintScanner.NO_FINGER && !isCancelled());
                enrolActivity.mScanner.finish();
                if (isCancelled()) {
                    break;
                }
                if (res.error != FingerprintScanner.RESULT_OK) {
                    enrolActivity.showErrorDialog(enrolActivity.getString(R.string.capture_image_failed), Util.getFingerprintErrorString(enrolActivity, res.error));
                    break;
                }
                fi = (FingerprintImage) res.data;
                Log.i(MainActivity.TAG, "Fingerprint image quality is " + Bione.getFingerprintQuality(fi));

            }

            if (params[0].equals("enroll")) {
                enrolActivity.showProgressDialog(enrolActivity.getString(R.string.loading), enrolActivity.getString(R.string.enrolling));
            }
            if (params[0].equals("enroll")) {
                res = Bione.extractFeature(fi);
                if (res.error != Bione.RESULT_OK) {
                    enrolActivity.showErrorDialog(enrolActivity.getString(R.string.enroll_failed_because_of_extract_feature), Util.getFingerprintErrorString(enrolActivity, res.error));
                    break;
                }
                fpFeat = (byte[]) res.data;
            }

            if (params[0].equals("enroll")) {
                res = Bione.makeTemplate(fpFeat, fpFeat, fpFeat);
                if (res.error != Bione.RESULT_OK) {
                    enrolActivity.showErrorDialog(enrolActivity.getString(R.string.enroll_failed_because_of_make_template), Util.getFingerprintErrorString(enrolActivity, res.error));
                    break;
                }
                fpTemp = (byte[]) res.data;

                int id = Bione.getFreeID();
                enrolActivity.newUserId = id;
                if (id < 0) {
                    enrolActivity.showErrorDialog(enrolActivity.getString(R.string.enroll_failed_because_of_get_id), Util.getFingerprintErrorString(enrolActivity, id));
                    break;

                }
                int ret = Bione.enroll(id, fpTemp);


                if (ret != Bione.RESULT_OK) {
                    enrolActivity.showErrorDialog(enrolActivity.getString(R.string.enroll_failed_because_of_error), Util.getFingerprintErrorString(enrolActivity, ret));
                    Toast.makeText(enrolActivity, "Did not Enroll", Toast.LENGTH_SHORT).show();
                    break;
                }
                enrolActivity.mId = id;
                enrolActivity.showInfoToast(enrolActivity.getString(R.string.enroll_success) + id);
                Toast.makeText(enrolActivity, "Success", Toast.LENGTH_SHORT).show();
            }

        } while (false);

        enrolActivity.dismissProgressDialog();
        mIsDone = true;
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (enrolActivity.choice.equals("enroll")) {
            try {

                final RealmModel user = new RealmModel(); // Create a new object
                user.setName(enrolActivity.nameTxt.getText().toString());
                user.setId(enrolActivity.newUserId);
                enrolActivity.realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(user);
                    }
                });
            } catch (Exception e) {
                CrashReporter.logException(e);
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onCancelled() {
    }

    public void waitForDone() {
        while (!mIsDone) {
        }
    }

    public void showProgressDialog(String title, String message, MainActivity mainActivity) {
        mainActivity.mHandler.sendMessage(mainActivity.mHandler.obtainMessage(MainActivity.MSG_SHOW_PROGRESS_DIALOG, new String[]{title, message}));
    }

}
