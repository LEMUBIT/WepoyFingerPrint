package com.lemuel.lemubit.fingerprinttest;

import android.annotation.SuppressLint;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.balsikandar.crashreporter.CrashReporter;
import com.wepoy.fp.Bione;
import com.wepoy.fp.FingerprintImage;
import com.wepoy.fp.FingerprintScanner;
import com.wepoy.util.Result;

import io.realm.Realm;
import io.realm.RealmResults;


@SuppressLint({"SdCardPath", "HandlerLeak"})
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FingerprintDemo";
    private static final String FP_DB_PATH = "/sdcard/fp.db";
    private static final int MSG_SHOW_ERROR = 0;
    private static final int MSG_SHOW_INFO = 1;
    private static final int MSG_UPDATE_IMAGE = 2;
    private static final int MSG_UPDATE_TEXT = 3;
    private static final int MSG_UPDATE_BUTTON = 4;
    private static final int MSG_UPDATE_SN = 5;
    private static final int MSG_UPDATE_FW_VERSION = 6;
    private static final int MSG_SHOW_PROGRESS_DIALOG = 7;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 8;
    private static final int MSG_FINISH_ACTIVITY = 9;
    Realm realm;
    private EditText nameTxt;
    private Button mBtnEnroll;
    private Button mBtnVerify;
    private Button mBtnIdentify;
    private Button mBtnClear;
    private Button mBtnShow;
    private ImageView mFingerprintImage;
    private ProgressDialog mProgressDialog;
    private FingerprintScanner mScanner;
    private FingerprintTask mTask;
    private int mId;
    private int newUserId = -1;
    String choice;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_ERROR: {
                    showDialog(0, (Bundle) msg.obj);
                    break;
                }
                case MSG_SHOW_INFO: {
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                case MSG_UPDATE_IMAGE: {
                    mFingerprintImage.setImageBitmap((Bitmap) msg.obj);
                    break;
                }
                case MSG_UPDATE_TEXT: {
                    break;
                }
                case MSG_UPDATE_BUTTON: {
                    Boolean enable = (Boolean) msg.obj;
                    mBtnEnroll.setEnabled(enable);
                    mBtnVerify.setEnabled(enable);
                    mBtnIdentify.setEnabled(enable);
                    mBtnClear.setEnabled(enable);
                    mBtnShow.setEnabled(enable);
                    break;
                }
                case MSG_UPDATE_SN: {
                    break;
                }
                case MSG_UPDATE_FW_VERSION: {
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
        setContentView(R.layout.activity_fingerprint);

        mScanner = FingerprintScanner.getInstance(getApplicationContext());
        mFingerprintImage = findViewById(R.id.fingerimage);
        nameTxt = findViewById(R.id.nameTxt);
        mBtnEnroll = findViewById(R.id.bt_enroll);
        mBtnVerify = findViewById(R.id.bt_verify);
        mBtnIdentify = findViewById(R.id.bt_identify);
        mBtnClear = findViewById(R.id.bt_clear);
        mBtnShow = findViewById(R.id.bt_show);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);

        updateSingerTestText(-1, -1, -1, -1);
        // Initialize Realm (just once per application)
        Realm.init(getApplicationContext());
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        openDevice();
    }

    @Override
    protected void onPause() {
        closeDevice(false);

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_enroll:
                mFingerprintImage.setVisibility(View.VISIBLE);
                enroll();
                break;
            case R.id.bt_verify:
                mFingerprintImage.setVisibility(View.VISIBLE);
                verify();
                break;
            case R.id.bt_identify:
                mFingerprintImage.setVisibility(View.VISIBLE);
                identify();
                break;
            case R.id.bt_clear:
                clearFingerprintDatabase();
                break;
//            case R.id.bt_show:
//                showFingerprintImage();
//                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                closeDevice(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateFingerprintImage(FingerprintImage fi) {
        byte[] fpBmp = null;
        Bitmap bitmap;
        if (fi == null || (fpBmp = fi.convert2Bmp()) == null || (bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length)) == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nofinger);
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_IMAGE, bitmap));
    }

    private void updateSingerTestText(long captureTime, long extractTime, long generalizeTime, long verifyTime) {
        String[] texts = new String[4];
        if (captureTime < 0) {
            texts[0] = getString(R.string.not_done);
        } else if (captureTime < 1) {
            texts[0] = "< 1ms";
        } else {
            texts[0] = captureTime + "ms";
        }

        if (extractTime < 0) {
            texts[1] = getString(R.string.not_done);
        } else if (extractTime < 1) {
            texts[1] = "< 1ms";
        } else {
            texts[1] = extractTime + "ms";
        }

        if (generalizeTime < 0) {
            texts[2] = getString(R.string.not_done);
        } else if (generalizeTime < 1) {
            texts[2] = "< 1ms";
        } else {
            texts[2] = generalizeTime + "ms";
        }

        if (verifyTime < 0) {
            texts[3] = getString(R.string.not_done);
        } else if (verifyTime < 1) {
            texts[3] = "< 1ms";
        } else {
            texts[3] = verifyTime + "ms";
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_TEXT, texts));
    }

    private void enableControl(boolean enable) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_BUTTON, enable));
    }

    private void finishActivity() {
        mHandler.sendEmptyMessage(MSG_FINISH_ACTIVITY);
    }

    private void openDevice() {
        new Thread() {
            @Override
            public void run() {
                showProgressDialog(getString(R.string.loading), getString(R.string.preparing_device));
                int error;
                if ((error = mScanner.powerOn()) != FingerprintScanner.RESULT_OK) {
                    showErrorDialog(getString(R.string.fingerprint_device_power_on_failed), getFingerprintErrorString(error));
                }
                if ((error = mScanner.open()) != FingerprintScanner.RESULT_OK) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SN, getString(R.string.fps_sn, "null")));
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_FW_VERSION, getString(R.string.fps_fw, "null")));
                    showErrorDialog(getString(R.string.fingerprint_device_open_failed), getFingerprintErrorString(error));
                } else {
                    Result res = mScanner.getSN();
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SN, getString(R.string.fps_sn, (String) res.data)));
                    res = mScanner.getFirmwareVersion();
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_FW_VERSION, getString(R.string.fps_fw, (String) res.data)));
                    showInfoToast(getString(R.string.fingerprint_device_open_success));
                    enableControl(true);
                }
                if ((error = Bione.initialize(MainActivity.this, FP_DB_PATH)) != Bione.RESULT_OK) {
                    showErrorDialog(getString(R.string.algorithm_initialization_failed), getFingerprintErrorString(error));
                }
                Log.i(TAG, "Fingerprint algorithm version: " + Bione.getVersion());
                dismissProgressDialog();
            }
        }.start();
    }

    private void closeDevice(final boolean finish) {
        new Thread() {
            @Override
            public void run() {
                showProgressDialog(getString(R.string.loading), getString(R.string.closing_device));
                enableControl(false);
                int error;
                if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
                    mTask.cancel(false);
                    mTask.waitForDone();
                }
                if ((error = mScanner.close()) != FingerprintScanner.RESULT_OK) {
                    showErrorDialog(getString(R.string.fingerprint_device_close_failed), getFingerprintErrorString(error));
                } else {
                    showInfoToast(getString(R.string.fingerprint_device_close_success));
                }
                if ((error = mScanner.powerOff()) != FingerprintScanner.RESULT_OK) {
                    showErrorDialog(getString(R.string.fingerprint_device_power_off_failed), getFingerprintErrorString(error));
                }
                if ((error = Bione.exit()) != Bione.RESULT_OK) {
                    showErrorDialog(getString(R.string.algorithm_cleanup_failed), getFingerprintErrorString(error));
                }
                if (finish) {
                    finishActivity();
                }
                dismissProgressDialog();
            }
        }.start();
    }

    private void enroll() {
        mTask = new FingerprintTask();
        mTask.execute("enroll");
    }

    private void verify() {
        mTask = new FingerprintTask();
        mTask.execute("verify");
    }

    private void identify() {
        mTask = new FingerprintTask();
        mTask.execute("identify");
    }

    private void clearFingerprintDatabase() {
        int error = Bione.clear();
        if (error == Bione.RESULT_OK) {
            showInfoToast(getString(R.string.clear_fingerprint_database_success));
        } else {
            showErrorDialog(getString(R.string.clear_fingerprint_database_failed), getFingerprintErrorString(error));
        }
    }

    private void showFingerprintImage() {
        mTask = new FingerprintTask();
        mTask.execute("show");
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        String operation = args.getString("operation");
        String errString = args.getString("errString");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.info_error);
        if (errString != null && !errString.equals("")) {
            builder.setMessage(operation + "\n" + errString);
        } else {
            builder.setMessage(operation);
        }
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    private void showProgressDialog(String title, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, new String[]{title, message}));
    }

    private void dismissProgressDialog() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_DISMISS_PROGRESS_DIALOG));
    }

    private void showErrorDialog(String operation, String errString) {
        Bundle bundle = new Bundle();
        bundle.putString("operation", operation);
        bundle.putString("errString", errString);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_ERROR, bundle));
    }

    private void showInfoToast(String info) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_INFO, info));
    }

    private String getFingerprintErrorString(int error) {
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
        return getString(strid);
    }

     class FingerprintTask extends AsyncTask<String, Integer, Void> {
        private boolean mIsDone = false;

        @Override
        protected void onPreExecute() {
            enableControl(false);
        }

        @Override
        protected Void doInBackground(String... params) {
            long startTime, captureTime = -1, extractTime = -1, generalizeTime = -1, verifyTime = -1;
            FingerprintImage fi = null;
            byte[] fpFeat = null, fpTemp = null;
            Result res;
            choice = params[0];
            do {
                if (params[0].equals("show") || params[0].equals("enroll") || params[0].equals("verify") || params[0].equals("identify")) {
                    showProgressDialog(getString(R.string.loading), getString(R.string.press_finger));
                    mScanner.prepare();
                    do {
                        startTime = System.currentTimeMillis();
                        res = mScanner.capture();
                        captureTime = System.currentTimeMillis() - startTime;
                    } while (res.error == FingerprintScanner.NO_FINGER && !isCancelled());
                    mScanner.finish();
                    if (isCancelled()) {
                        break;
                    }
                    if (res.error != FingerprintScanner.RESULT_OK) {
                        showErrorDialog(getString(R.string.capture_image_failed), getFingerprintErrorString(res.error));
                        break;
                    }
                    fi = (FingerprintImage) res.data;
                    Log.i(TAG, "Fingerprint image quality is " + Bione.getFingerprintQuality(fi));

                }

                if (params[0].equals("enroll")) {
                    showProgressDialog(getString(R.string.loading), getString(R.string.enrolling));
                } else if (params[0].equals("verify")) {
                    showProgressDialog(getString(R.string.loading), getString(R.string.verifying));
                } else if (params[0].equals("identify")) {
                    showProgressDialog(getString(R.string.loading), getString(R.string.identifying));
                }

                if (params[0].equals("enroll") || params[0].equals("verify") || params[0].equals("identify")) {
                    startTime = System.currentTimeMillis();
                    res = Bione.extractFeature(fi);
                    extractTime = System.currentTimeMillis() - startTime;
                    if (res.error != Bione.RESULT_OK) {
                        showErrorDialog(getString(R.string.enroll_failed_because_of_extract_feature), getFingerprintErrorString(res.error));
                        break;
                    }
                    fpFeat = (byte[]) res.data;
                }

                if (params[0].equals("enroll")) {
                    startTime = System.currentTimeMillis();
                    res = Bione.makeTemplate(fpFeat, fpFeat, fpFeat);
                    generalizeTime = System.currentTimeMillis() - startTime;
                    if (res.error != Bione.RESULT_OK) {
                        showErrorDialog(getString(R.string.enroll_failed_because_of_make_template), getFingerprintErrorString(res.error));
                        break;
                    }
                    fpTemp = (byte[]) res.data;

                    int id = Bione.getFreeID();
                    newUserId = id;
                    if (id < 0) {
                        showErrorDialog(getString(R.string.enroll_failed_because_of_get_id), getFingerprintErrorString(id));
                        break;
                    }
                    int ret = Bione.enroll(id, fpTemp);


                    if (ret != Bione.RESULT_OK) {
                        showErrorDialog(getString(R.string.enroll_failed_because_of_error), getFingerprintErrorString(ret));
                        break;
                    }
                    mId = id;
                    showInfoToast(getString(R.string.enroll_success) + id);
                } else if (params[0].equals("verify")) {
                    startTime = System.currentTimeMillis();
                    res = Bione.verify(mId, fpFeat);
                    verifyTime = System.currentTimeMillis() - startTime;
                    if (res.error != Bione.RESULT_OK) {
                        showErrorDialog(getString(R.string.verify_failed_because_of_error), getFingerprintErrorString(res.error));
                        break;
                    }
                    if ((Boolean) res.data) {
                        showInfoToast(getString(R.string.fingerprint_match));
                    } else {
                        showInfoToast(getString(R.string.fingerprint_not_match));
                    }
                } else if (params[0].equals("identify")) {
                    startTime = System.currentTimeMillis();
                    int id = Bione.identify(fpFeat);
                    newUserId = id;
                    verifyTime = System.currentTimeMillis() - startTime;
                    if (id < 0) {
                        showErrorDialog(getString(R.string.identify_failed_because_of_error), getFingerprintErrorString(id));
                        break;
                    }

                    showInfoToast(getString(R.string.identify_match) + id);
                }

                if (params[0].equals("show") || params[0].equals("enroll") || params[0].equals("verify") || params[0].equals("identify")) {
                    updateFingerprintImage(fi);
                }
            } while (false);

            updateSingerTestText(captureTime, extractTime, generalizeTime, verifyTime);
            enableControl(true);
            dismissProgressDialog();
            mIsDone = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (choice.equals("enroll")) {
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
                } catch (Exception e) {
                    CrashReporter.logException(e);
                }
            }
            if (choice.equals("identify")) {
                if (newUserId >= 0) {
                    RealmResults<RealmModel> realmResult = realm.where(RealmModel.class)
                            .equalTo("id", newUserId).findAll();
                    //todo continue from here
                    Toast.makeText(MainActivity.this, "Welcome " + realmResult.get(0).getName(), Toast.LENGTH_SHORT).show();
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
    }
}
