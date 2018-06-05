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
import io.realm.RealmResults;

class FingerprintTask extends AsyncTask<String, Integer, Void> {
    private MainActivity mainActivity;
    private boolean mIsDone = false;

    public FingerprintTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
   protected void onPreExecute() {
       mainActivity.enableControl(false);
   }

   @Override
   protected Void doInBackground(String... params) {
       long startTime, captureTime = -1, extractTime = -1, generalizeTime = -1, verifyTime = -1;
       FingerprintImage fi = null;
       byte[] fpFeat = null, fpTemp = null;
       Result res;
       mainActivity.choice = params[0];
       do {
           if (params[0].equals("show") || params[0].equals("enroll") || params[0].equals("verify") || params[0].equals("identify")) {
               mainActivity.showProgressDialog(mainActivity.getString(R.string.loading), mainActivity.getString(R.string.press_finger));
               mainActivity.mScanner.prepare();
               do {
                   startTime = System.currentTimeMillis();
                   res = mainActivity.mScanner.capture();
                   captureTime = System.currentTimeMillis() - startTime;
               } while (res.error == FingerprintScanner.NO_FINGER && !isCancelled());
               mainActivity.mScanner.finish();
               if (isCancelled()) {
                   break;
               }
               if (res.error != FingerprintScanner.RESULT_OK) {
                   mainActivity.showErrorDialog(mainActivity.getString(R.string.capture_image_failed), Util.getFingerprintErrorString(mainActivity, res.error));
                   break;
               }
               fi = (FingerprintImage) res.data;
               Log.i(MainActivity.TAG, "Fingerprint image quality is " + Bione.getFingerprintQuality(fi));

           }

           if (params[0].equals("enroll")) {
               mainActivity.showProgressDialog(mainActivity.getString(R.string.loading), mainActivity.getString(R.string.enrolling));
           } else if (params[0].equals("verify")) {
               mainActivity.showProgressDialog(mainActivity.getString(R.string.loading), mainActivity.getString(R.string.verifying));
           } else if (params[0].equals("identify")) {
               mainActivity.showProgressDialog(mainActivity.getString(R.string.loading), mainActivity.getString(R.string.identifying));
           }

           if (params[0].equals("enroll") || params[0].equals("verify") || params[0].equals("identify")) {
               startTime = System.currentTimeMillis();
               res = Bione.extractFeature(fi);
               extractTime = System.currentTimeMillis() - startTime;
               if (res.error != Bione.RESULT_OK) {
                   mainActivity.showErrorDialog(mainActivity.getString(R.string.enroll_failed_because_of_extract_feature), Util.getFingerprintErrorString(mainActivity, res.error));
                   break;
               }
               fpFeat = (byte[]) res.data;
           }

           if (params[0].equals("enroll")) {
               startTime = System.currentTimeMillis();
               res = Bione.makeTemplate(fpFeat, fpFeat, fpFeat);
               generalizeTime = System.currentTimeMillis() - startTime;
               if (res.error != Bione.RESULT_OK) {
                   mainActivity.showErrorDialog(mainActivity.getString(R.string.enroll_failed_because_of_make_template), Util.getFingerprintErrorString(mainActivity, res.error));
                   break;
               }
               fpTemp = (byte[]) res.data;

               int id = Bione.getFreeID();
               mainActivity.newUserId = id;
               if (id < 0) {
                   mainActivity.showErrorDialog(mainActivity.getString(R.string.enroll_failed_because_of_get_id), Util.getFingerprintErrorString(mainActivity, id));
                   break;

               }
               int ret = Bione.enroll(id, fpTemp);


               if (ret != Bione.RESULT_OK) {
                   mainActivity.showErrorDialog(mainActivity.getString(R.string.enroll_failed_because_of_error), Util.getFingerprintErrorString(mainActivity, ret));
                   break;
               }
               mainActivity.mId = id;
               mainActivity.showInfoToast(mainActivity.getString(R.string.enroll_success) + id);
           } else if (params[0].equals("verify")) {
               startTime = System.currentTimeMillis();
               res = Bione.verify(mainActivity.mId, fpFeat);
               verifyTime = System.currentTimeMillis() - startTime;
               if (res.error != Bione.RESULT_OK) {
                   mainActivity.showErrorDialog(mainActivity.getString(R.string.verify_failed_because_of_error), Util.getFingerprintErrorString(mainActivity, res.error));
                   break;
               }
               if ((Boolean) res.data) {
                   mainActivity.showInfoToast(mainActivity.getString(R.string.fingerprint_match));
               } else {
                   mainActivity.showInfoToast(mainActivity.getString(R.string.fingerprint_not_match));
               }
           } else if (params[0].equals("identify")) {
               startTime = System.currentTimeMillis();
               int id = Bione.identify(fpFeat);
               mainActivity.newUserId = id;
               verifyTime = System.currentTimeMillis() - startTime;
               if (id < 0) {
                   mainActivity.showErrorDialog(mainActivity.getString(R.string.identify_failed_because_of_error), Util.getFingerprintErrorString(mainActivity, id));
                   break;
               }

               //mainActivity.showInfoToast(mainActivity.getString(R.string.identify_match) + id);
           }

           if (params[0].equals("show") || params[0].equals("enroll") || params[0].equals("verify") || params[0].equals("identify")) {
               Util.updateFingerprintImage(mainActivity, fi);
           }
       } while (false);


       mainActivity.enableControl(true);
       mainActivity.dismissProgressDialog();
       mIsDone = true;
       return null;
   }

   @Override
   protected void onPostExecute(Void result) {
       if (mainActivity.choice.equals("enroll")) {
           try {

               final RealmModel user = new RealmModel(); // Create a new object
               user.setName(mainActivity.nameTxt.getText().toString());
               user.setId(mainActivity.newUserId);
               mainActivity.realm.executeTransaction(new Realm.Transaction() {
                   @Override
                   public void execute(Realm realm) {
                       realm.copyToRealmOrUpdate(user);
                   }
               });
           } catch (Exception e) {
               CrashReporter.logException(e);
           }
       }
       if (mainActivity.choice.equals("identify")) {
           if (mainActivity.newUserId >= 0) {
               RealmResults<RealmModel> realmResult = mainActivity.realm.where(RealmModel.class)
                       .equalTo("id", mainActivity.newUserId).findAll();
               Toast.makeText(mainActivity, "Welcome " + realmResult.get(0).getName(), Toast.LENGTH_SHORT).show();
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

    public void showProgressDialog(String title, String message, EnrolActivity enrolActivity) {
        enrolActivity.mHandler.sendMessage(enrolActivity.mHandler.obtainMessage(MainActivity.MSG_SHOW_PROGRESS_DIALOG, new String[]{title, message}));
    }
}
