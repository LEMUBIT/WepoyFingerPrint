package com.lemuel.lemubit.fingerprinttest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.balsikandar.crashreporter.CrashReporter;
import com.bugsnag.android.Bugsnag;
import com.wepoy.fp.Bione;
import com.wepoy.fp.FingerprintImage;
import com.wepoy.fp.FingerprintScanner;
import com.wepoy.util.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

@SuppressLint({"SdCardPath", "HandlerLeak"})
public class EnrolActivity extends AppCompatActivity {
    public EnrollTask mTask;
    private Realm realm;
    private Boolean fingerprintGood;
    public FingerprintScanner mScanner;
    public int mId;
    public int newUserId = -1;
    private String choice;
    public static final String TAG = "FingerprintDemo";
    public static final String FP_DB_PATH = "/sdcard/fp.db";
    private static final int MSG_SHOW_ERROR = 0;
    private static final int MSG_SHOW_INFO = 1;
    protected static final int MSG_SHOW_PROGRESS_DIALOG = 7;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 8;
    private static final int MSG_FINISH_ACTIVITY = 9;
    private ProgressDialog mProgressDialog;

    @BindView(R.id.txt_name)
    EditText nameTxt;
    @BindView(R.id.txt_surname)
    EditText surNameTxt;
    @BindView(R.id.txt_jobrole)
    EditText jobRoleTxt;
    @BindView(R.id.btn_capture)
    Button captureBtn;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_ERROR: {
                    showDialog(0, (Bundle) msg.obj);
                    break;
                }
                case MSG_SHOW_INFO: {
                    Toast.makeText(EnrolActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }

                case MSG_SHOW_PROGRESS_DIALOG: {
                    String[] info = (String[]) msg.obj;
                    mProgressDialog.setTitle(info[0]);
                    mProgressDialog.setMessage(info[1]);
                    mProgressDialog.show();
                    break;
                }
                case MSG_DISMISS_PROGRESS_DIALOG: {
                    mProgressDialog.dismiss();
                    break;
                }
                case MSG_FINISH_ACTIVITY: {
                    finish();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrol);
        ButterKnife.bind(this);
        mScanner = FingerprintScanner.getInstance(getApplicationContext());

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        captureBtn.setOnClickListener(v -> {
            mTask = new EnrollTask();
            mTask.execute("enroll");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.openDevice(this);
        Toast.makeText(this, "Testing in main class", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Util.closeDevice(this, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Util.closeDevice(this, true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void dismissProgressDialog() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_DISMISS_PROGRESS_DIALOG));
    }

    public void showErrorDialog(String operation, String errString) {
        Bundle bundle = new Bundle();
        bundle.putString("operation", operation);
        bundle.putString("errString", errString);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_ERROR, bundle));
    }

    public void showInfoToast(String info) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_INFO, info));
    }

    public void finishActivity() {
        mHandler.sendEmptyMessage(MSG_FINISH_ACTIVITY);
    }

    public void showProgressDialog(String title, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, new String[]{title, message}));
    }

    class EnrollTask extends AsyncTask<String, Integer, Void> {

        private boolean mIsDone = false;

        public EnrollTask() {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {

            FingerprintImage fi = null;
            byte[] fpFeat = null, fpTemp = null;
            Result res;
            choice = params[0];

            do {
                showProgressDialog(getString(R.string.loading), getString(R.string.press_finger));
                mScanner.prepare();
                do {
                    res = mScanner.capture();
                } while (res.error == FingerprintScanner.NO_FINGER && !isCancelled());
                mScanner.finish();

                if (isCancelled()) {
                    Bugsnag.leaveBreadcrumb("Finger print canceled");
                    break;
                }

                if (res.error != FingerprintScanner.RESULT_OK) {
                    showInfoToast(getString(R.string.capture_image_failed));
                    Bugsnag.notify(new Exception());
                    break;
                }

                fi = (FingerprintImage) res.data;

                //Enrolling
                showProgressDialog(getString(R.string.loading), getString(R.string.enrolling));
                Bugsnag.leaveBreadcrumb(getString(R.string.enrolling));


                //Extract feature
                res = Bione.extractFeature(fi);

                if (res.error != Bione.RESULT_OK) {
                    showInfoToast(getString(R.string.enroll_failed_because_of_extract_feature));
                    fingerprintGood = false;
                    Bugsnag.leaveBreadcrumb(getString(R.string.enroll_failed_because_of_extract_feature));
                    Bugsnag.notify(new Exception());
                    break;
                }

                fpFeat = (byte[]) res.data;


                res = Bione.makeTemplate(fpFeat, fpFeat, fpFeat);

                if (res.error != Bione.RESULT_OK) {
                    showInfoToast(getString(R.string.enroll_failed_because_of_make_template));
                    fingerprintGood = false;
                    Bugsnag.leaveBreadcrumb(getString(R.string.enroll_failed_because_of_make_template));
                    Bugsnag.notify(new Exception());
                    break;
                }

                fpTemp = (byte[]) res.data;


                int id = Bione.getFreeID();
                newUserId = id;
                if (id < 0) {
                    showInfoToast(getString(R.string.enroll_failed_because_of_get_id));
                    fingerprintGood = false;
                    Bugsnag.leaveBreadcrumb(getString(R.string.enroll_failed_because_of_get_id));
                    Bugsnag.notify(new Exception());
                    break;

                }
                int ret = Bione.enroll(id, fpTemp);


                if (ret != Bione.RESULT_OK) {
                    showInfoToast(getString(R.string.enroll_failed_because_of_error));
                    fingerprintGood = false;
                    Bugsnag.leaveBreadcrumb(getString(R.string.enroll_failed_because_of_error));
                    Bugsnag.notify(new Exception());
                    break;
                }
                mId = id;

                showInfoToast(getString(R.string.enroll_success) + id);
                Bugsnag.leaveBreadcrumb(getString(R.string.enroll_success) + id);
                fingerprintGood = true;


            } while (false);

            dismissProgressDialog();
            mIsDone = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (choice.equals("enroll") && fingerprintGood) {
                try {

                    final RealmModel user = new RealmModel(); // Create a new object

                    user.setName(nameTxt.getText().toString());
                    user.setId(newUserId);

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(user);
                        }
                    });

                    Toast.makeText(EnrolActivity.this, "Enrolled in Realm", Toast.LENGTH_SHORT).show();
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

        public void showProgressDialog(String title, String message) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, new String[]{title, message}));
        }

    }
}
