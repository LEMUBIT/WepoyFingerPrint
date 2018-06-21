package com.lemuel.lemubit.fingerprinttest.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.model.Fingerprint
import com.lemuel.lemubit.fingerprinttest.model.modelInterface.fingerPrintInterface
import com.lemuel.lemubit.fingerprinttest.presenter.EnrolPresenter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_enrol.*

@SuppressLint("SdCardPath", "HandlerLeak")
class EnrolActivity : AppCompatActivity(), fingerPrintInterface {
    private var realm: Realm? = null
    private var mProgressDialog: ProgressDialog? = null
    lateinit var enrolPresenter: EnrolPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrol)


        realm = Realm.getDefaultInstance()

        enrolPresenter = EnrolPresenter(this)

        //?using just one fingerprint for now
        val fingerObserver = Observable.defer {
            Observable.just(Fingerprint.getFingerPrint(application, this))
                    .map { result -> Fingerprint.saveFingerPrint(application, result, this) }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

        btn_capture.setOnClickListener { fingerObserver.subscribe(observer) }

    }

    override fun setProgressDialog() {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog!!.setIcon(android.R.drawable.ic_dialog_info)
        mProgressDialog!!.isIndeterminate = false
        mProgressDialog!!.setCancelable(false)
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

    fun finishActivity() {
        mHandler.sendEmptyMessage(MSG_FINISH_ACTIVITY)
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
            showInfoToast(enrolPresenter.registerNewUserInLocalDB(application, id, txt_name.text.toString()))
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
