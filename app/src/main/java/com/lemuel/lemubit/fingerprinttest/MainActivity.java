package com.lemuel.lemubit.fingerprinttest;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.wepoy.fp.Bione;
import com.wepoy.fp.FingerprintScanner;
import com.wepoy.util.Result;

import io.realm.Realm;


@SuppressLint({"SdCardPath", "HandlerLeak"})
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "FingerprintDemo";
    public static final String FP_DB_PATH = "/sdcard/fp.db";
    private static final int MSG_SHOW_ERROR = 0;
    private static final int MSG_SHOW_INFO = 1;
    public static final int MSG_UPDATE_IMAGE = 2;
    private static final int MSG_UPDATE_TEXT = 3;
    private static final int MSG_UPDATE_BUTTON = 4;
    public static final int MSG_UPDATE_SN = 5;
    public static final int MSG_UPDATE_FW_VERSION = 6;
    private static final int MSG_SHOW_PROGRESS_DIALOG = 7;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 8;
    private static final int MSG_FINISH_ACTIVITY = 9;
    Realm realm;
    public EditText nameTxt;
    private Button mBtnEnroll;
    private Button mBtnVerify;
    private Button mBtnIdentify;
    private Button mBtnClear;
    private Button mBtnShow;
    private ImageView mFingerprintImage;
    private ProgressDialog mProgressDialog;
    public FingerprintScanner mScanner;
    public FingerprintTask mTask;
    public int mId;
    public int newUserId = -1;
    String choice;
    public Handler mHandler = new Handler() {
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

    void openDevice() {
        new Thread() {
            @Override
            public void run() {
                showProgressDialog(getString(R.string.loading), getString(R.string.preparing_device));
                int error;
                if ((error = mScanner.powerOn()) != FingerprintScanner.RESULT_OK) {
                    showErrorDialog(getString(R.string.fingerprint_device_power_on_failed), Util.getFingerprintErrorString(MainActivity.this, error));
                }
                if ((error = mScanner.open()) != FingerprintScanner.RESULT_OK) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SN, getString(R.string.fps_sn, "null")));
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_FW_VERSION, getString(R.string.fps_fw, "null")));
                    showErrorDialog(getString(R.string.fingerprint_device_open_failed), Util.getFingerprintErrorString(MainActivity.this, error));
                } else {
                    Result res = mScanner.getSN();
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SN, getString(R.string.fps_sn, (String) res.data)));
                    res = mScanner.getFirmwareVersion();
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_FW_VERSION, getString(R.string.fps_fw, (String) res.data)));
                    showInfoToast(getString(R.string.fingerprint_device_open_success));
                    enableControl(true);
                }
                if ((error = Bione.initialize(MainActivity.this, FP_DB_PATH)) != Bione.RESULT_OK) {
                    showErrorDialog(getString(R.string.algorithm_initialization_failed), Util.getFingerprintErrorString(MainActivity.this, error));
                }
                Log.i(TAG, "Fingerprint algorithm version: " + Bione.getVersion());
                dismissProgressDialog();
            }
        }.start();
    }

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

        // Initialize Realm (just once per application)
        Realm.init(getApplicationContext());
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.openDevice();
    }

    @Override
    protected void onPause() {
        Util.closeDevice(this, false);
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
        }
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

    public void enableControl(boolean enable) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_BUTTON, enable));
    }

    public void finishActivity() {
        mHandler.sendEmptyMessage(MSG_FINISH_ACTIVITY);
    }

    private void enroll() {
        mTask = new FingerprintTask(this);
        mTask.execute("enroll");
    }

    private void verify() {
        mTask = new FingerprintTask(this);
        mTask.execute("verify");
    }

    private void identify() {
        mTask = new FingerprintTask(this);
        mTask.execute("identify");
    }

    private void clearFingerprintDatabase() {
        int error = Bione.clear();
        if (error == Bione.RESULT_OK) {
            showInfoToast(getString(R.string.clear_fingerprint_database_success));
        } else {
            showErrorDialog(getString(R.string.clear_fingerprint_database_failed), Util.getFingerprintErrorString(this, error));
        }
    }

    private void showFingerprintImage() {
        mTask = new FingerprintTask(this);
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
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
        return builder.create();
    }

    public void showProgressDialog(String title, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, new String[]{title, message}));
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

}
