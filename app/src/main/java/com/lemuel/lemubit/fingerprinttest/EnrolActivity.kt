package com.lemuel.lemubit.fingerprinttest

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import com.wepoy.fp.FingerprintScanner
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_enrol.*
import org.reactivestreams.Subscription

@SuppressLint("SdCardPath", "HandlerLeak")
class EnrolActivity : AppCompatActivity(), EnrolView {

    private var mIsDone = false
    private var realm: Realm? = null
    private var fingerprintGood: Boolean? = null
    lateinit var mScanner: FingerprintScanner
    var mId: Int = 0
    var newUserId = -1
    private var choice: String? = null
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrol)
        mScanner = FingerprintScanner.getInstance(applicationContext)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog!!.setIcon(android.R.drawable.ic_dialog_info)
        mProgressDialog!!.isIndeterminate = false
        mProgressDialog!!.setCancelable(false)

        realm = Realm.getDefaultInstance()

        //TODO next is to try three times
        val fingerObserver = Observable.defer {
            Observable.just(Fingerprint.getFingerPrint(application, this)).take(3)
                    .map { t -> Fingerprint.saveFingerPrint(application, t, this) }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

        btn_capture.setOnClickListener { fingerObserver.subscribe(observer) }
    }

    override fun executionDone(done: Boolean?) {
        mIsDone = done!!
    }

    override fun onResume() {
        super.onResume()
        Util.openDevice(this)
        Toast.makeText(this, "Testing in main class", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        Util.closeDevice(this, false)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                Util.closeDevice(this, true)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    fun finishActivity() {
        mHandler.sendEmptyMessage(MSG_FINISH_ACTIVITY)
    }

    override fun showProgressDialog(title: String, message: String) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, arrayOf(title, message)))
    }

    override fun dismissProgressDialog() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_DISMISS_PROGRESS_DIALOG))
    }

    override fun showInfoToast(info: String) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_INFO, info))
    }


    companion object {
        val TAG = "FingerprintDemo"
        val FP_DB_PATH = "/sdcard/fp.db"
        private val MSG_SHOW_ERROR = 0
        private val MSG_SHOW_INFO = 1
        protected val MSG_SHOW_PROGRESS_DIALOG = 7
        private val MSG_DISMISS_PROGRESS_DIALOG = 8
        private val MSG_FINISH_ACTIVITY = 9
    }

    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SHOW_ERROR -> {
                    showDialog(0, msg.obj as Bundle)
                }
                MSG_SHOW_INFO -> {
                    Toast.makeText(this@EnrolActivity, msg.obj as String, Toast.LENGTH_SHORT).show()
                }

                MSG_SHOW_PROGRESS_DIALOG -> {
                    val info = msg.obj as Array<String>
                    mProgressDialog!!.setTitle(info[0])
                    mProgressDialog!!.setMessage(info[1])
                    mProgressDialog!!.show()
                }
                MSG_DISMISS_PROGRESS_DIALOG -> {
                    mProgressDialog!!.dismiss()
                }
                MSG_FINISH_ACTIVITY -> {
                    finish()
                }
            }
        }
    }

    private val observer = object : Observer<Int> {
        override fun onNext(id: Int) {
            showInfoToast("ID= $id")
            try {
                val user = RealmModel() // Create a new object
                user.name = txt_name.text.toString()
                user.id = id
                realm!!.executeTransaction { realm -> realm.copyToRealmOrUpdate(user) }
                Toast.makeText(this@EnrolActivity, "Enrolled in Realm", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                showInfoToast("Realm Error")
            }
        }

        override fun onComplete() {
            showInfoToast("Completed")
        }

        override fun onSubscribe(d: Disposable) {
            showInfoToast("subscribed")
        }


        override fun onError(e: Throwable) {
            showInfoToast("Error received: " + e.message)
        }

    }
}
