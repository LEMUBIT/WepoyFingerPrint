package com.lemuel.lemubit.fingerprinttest

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast

import com.bugsnag.android.Bugsnag
import com.wepoy.fp.Bione
import com.wepoy.fp.FingerprintImage
import com.wepoy.fp.FingerprintScanner
import com.wepoy.util.Result
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_enrol.*

@SuppressLint("SdCardPath", "HandlerLeak")
class EnrolActivity : AppCompatActivity(), EnrolView {

    private var mIsDone = false
    var mTask: EnrollTask? = null
    private var realm: Realm? = null
    private var fingerprintGood: Boolean? = null
    lateinit var mScanner: FingerprintScanner
    var mId: Int = 0
    var newUserId = -1
    private var choice: String? = null
    private var mProgressDialog: ProgressDialog? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrol)
        mScanner = FingerprintScanner.getInstance(applicationContext)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog!!.setIcon(android.R.drawable.ic_dialog_info)
        mProgressDialog!!.isIndeterminate = false
        mProgressDialog!!.setCancelable(false)
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance()
        btn_capture.setOnClickListener { v ->
            //mTask = new EnrollTask();
            // mTask.execute("enroll");

            //todo CONTINUE HERE
            //RxJava used to get fingerprint
            Observable.defer { Observable.just(Fingerprint.getFingerPrint(application, this)) }
                    .subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe { fingerPrintResponse ->

                        if (fingerPrintResponse.state == FingerPrintResponse.FINGER_RESPONSE_GOOD && fingerPrintResponse.res != null) {
                            Toast.makeText(this, "Good finger print ${fingerPrintResponse.state}", Toast.LENGTH_SHORT).show()
//                            mTask = EnrollTask(fingerPrintResponse)
//                            mTask!!.execute("enroll")
                        } else {
                            Toast.makeText(this, "Bad finger print", Toast.LENGTH_SHORT).show()
                        }

                    }
        }
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


    fun showErrorDialog(operation: String, errString: String) {
        val bundle = Bundle()
        bundle.putString("operation", operation)
        bundle.putString("errString", errString)
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_ERROR, bundle))
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


    inner class EnrollTask(fingerPrintResponse: FingerPrintResponse?) : AsyncTask<String, Int, Void>() {
        var res: Result?

        init {
            this.res = fingerPrintResponse?.res
        }

        override fun onPreExecute() {}

        override fun doInBackground(vararg params: String): Void? {

            val fi: FingerprintImage? = null
            var fpFeat: ByteArray? = null
            var fpTemp: ByteArray? = null

            choice = params[0]
            val fingerPrintCount = 0
            do {
                val x = 0
                //Extract byte data gotten from Bione Feature and make Template
                fpFeat = res!!.data as ByteArray
                res = Bione.makeTemplate(fpFeat, fpFeat, fpFeat)

                if (res!!.error != Bione.RESULT_OK) {
                    showInfoToast(getString(R.string.enroll_failed_because_of_make_template))
                    fingerprintGood = false
                    Bugsnag.leaveBreadcrumb(getString(R.string.enroll_failed_because_of_make_template))
                    Bugsnag.notify(Exception())
                    break
                }

                fpTemp = res!!.data as ByteArray

                val id = Bione.getFreeID()
                newUserId = id
                if (id < 0) {
                    showInfoToast(getString(R.string.enroll_failed_because_of_get_id))
                    fingerprintGood = false
                    Bugsnag.leaveBreadcrumb(getString(R.string.enroll_failed_because_of_get_id))
                    Bugsnag.notify(Exception())
                    break
                }
                val ret = Bione.enroll(id, fpTemp)

                if (ret != Bione.RESULT_OK) {
                    showInfoToast(getString(R.string.enroll_failed_because_of_error))
                    fingerprintGood = false
                    Bugsnag.leaveBreadcrumb(getString(R.string.enroll_failed_because_of_error))
                    Bugsnag.notify(Exception())
                    break
                }
                mId = id
                showInfoToast(getString(R.string.enroll_success) + id)
                Bugsnag.leaveBreadcrumb(getString(R.string.enroll_success) + id)
                fingerprintGood = true

            } while (false)

            dismissProgressDialog()
            return null
        }


        override fun onPostExecute(result: Void) {
            if (choice == "enroll" && fingerprintGood!!) {
                //todo: Perform database operation in Model
                try {
                    val user = RealmModel() // Create a new object
                    user.name = txt_name.text.toString()
                    user.id = newUserId
                    realm!!.executeTransaction { realm -> realm.copyToRealmOrUpdate(user) }
                    Toast.makeText(this@EnrolActivity, "Enrolled in Realm", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {

                }

            }
        }

        override fun onCancelled() {}


        fun waitForDone() {
            while (!mIsDone) {
            }
        }
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
}
