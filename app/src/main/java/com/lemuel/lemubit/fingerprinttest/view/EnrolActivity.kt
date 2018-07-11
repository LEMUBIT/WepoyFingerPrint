package com.lemuel.lemubit.fingerprinttest.view

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.operations.Fingerprint
import com.lemuel.lemubit.fingerprinttest.presenter.EnrolPresenter
import com.lemuel.lemubit.fingerprinttest.viewInterface.EnrolActivityView
import com.lemuel.lemubit.fingerprinttest.viewInterface.FingerPrintInterface
import com.mapzen.speakerbox.Speakerbox
import com.wepoy.fp.FingerprintImage
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_enrol.*


@SuppressLint("SdCardPath", "HandlerLeak")
class EnrolActivity : AppCompatActivity(), EnrolActivityView, FingerPrintInterface {
    private var realm: Realm? = null
    lateinit var dialogBuilder: MaterialDialog.Builder
    lateinit var dialog: MaterialDialog
    lateinit var mPlayer: MediaPlayer
    var mPlayerInitialized = false
    var rightFingerIDMap = HashMap<Int, Int>()
    lateinit var speakerbox: Speakerbox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrol)

        setProgressDialog()
        speakerbox = Speakerbox(application)
        speakerbox.enableVolumeControl(this)
        realm = Realm.getDefaultInstance()

        //?using just one fingerprint for now
        val fingerObserver = Observable.defer {
            Observable.just(Fingerprint.getFingerPrint(application, this))
                    .map { result -> Fingerprint.saveFingerPrint(application, result, this) }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

        btn_capture.setOnClickListener { startActivityForResult(Intent(this, CaptureFingers::class.java),1) }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayerInitialized) {
            mPlayer.release()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //todo check out data intent
        super.onActivityResult(requestCode, resultCode, data)
        speakerbox.play("Finger information gotten")
        val bundle = this.intent.extras
        if(bundle != null) {
            rightFingerIDMap = bundle.getSerializable("FingerIDHashMap") as HashMap<Int, Int>;
        }
        //todo continue form here... size was zero check out data instead

        speakerbox.play("Size is ${rightFingerIDMap.size}")
    }

    override fun setProgressDialog() {
        dialogBuilder = MaterialDialog.Builder(this)
                .title(R.string.enrol)
                .content(R.string.press_finger)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
        dialog = dialogBuilder.build()

    }

    override fun onPlayNotificationSound(res: Int) {
        mPlayer = MediaPlayer.create(this@EnrolActivity, res)
        mPlayer.start()
        mPlayerInitialized = true
    }

    override fun updateFingerPrintImage(fi: FingerprintImage?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgressDialog(title: String, message: String) {
        this.runOnUiThread { dialog.show() }
    }

    override fun dismissProgressDialog() {
        this.runOnUiThread { dialog.dismiss() }
    }

    override fun showInfoToast(info: String) {
        this.runOnUiThread { Toast.makeText(this@EnrolActivity, info, Toast.LENGTH_SHORT).show() }
    }

    companion object;

    private val observer = object : Observer<Int> {
        override fun onNext(id: Int) {
            if (id >= 0) {
                showInfoToast(EnrolPresenter.registerNewUserInLocalDB(id, txt_name.text.toString(), txt_surname.text.toString()))
                EnrolPresenter.playSound(EnrolPresenter.GOOD, this@EnrolActivity)
            } else {
                //A check is put in place here because sometimes when the fingerprint is not gotten
                //an negative integer is returned.
                EnrolPresenter.playSound(EnrolPresenter.BAD, this@EnrolActivity)
            }
        }

        override fun onComplete() {
            Log.d("RxJAVA:", "completed")
        }

        override fun onSubscribe(d: Disposable) {
            Log.d("RxJAVA:", "subscribed")
        }

        override fun onError(e: Throwable) {
            Log.d("RxJAVA:", e.message)
            EnrolPresenter.playSound(EnrolPresenter.BAD, this@EnrolActivity)
        }

    }
}
