package com.lemuel.lemubit.fingerprinttest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wepoy.fp.FingerprintScanner;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

//todo get dialog to show again when checking finger print
public class EnrolActivity extends AppCompatActivity {
    public EnrollTask mTask;
    public static final String TAG = "FingerprintDemo";
    public static final String FP_DB_PATH = "/sdcard/fp.db";
    private static final int MSG_SHOW_ERROR = 0;
    private static final int MSG_SHOW_INFO = 1;
    public static final int MSG_SHOW_PROGRESS_DIALOG = 7;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 8;
    private static final int MSG_FINISH_ACTIVITY = 9;
    Realm realm;
    public FingerprintScanner mScanner;
    public int mId;
    public int newUserId = -1;
    String choice;
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
                    break;
                }
                case MSG_DISMISS_PROGRESS_DIALOG: {
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
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        mTask = new EnrollTask(this);
        captureBtn.setOnClickListener(v -> {
            mTask.execute("enroll");
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        Util.openDevice(this);
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
        mHandler.sendMessage(mHandler.obtainMessage(MainActivity.MSG_SHOW_PROGRESS_DIALOG, new String[]{title, message}));
    }
}
