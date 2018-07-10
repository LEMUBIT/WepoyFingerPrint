package com.lemuel.lemubit.fingerprinttest.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog

import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.operations.Fingerprint
import com.lemuel.lemubit.fingerprinttest.viewInterface.FingerPrintInterface
import com.mapzen.speakerbox.Speakerbox
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_capture_fingers.*

class CaptureFingers : AppCompatActivity(), FingerPrintInterface {

    var fingerIDMap = HashMap<Int, Int>()
    var fingerCount = 1
    lateinit var dialogBuilder: MaterialDialog.Builder
    lateinit var dialog: MaterialDialog
    lateinit var speakerbox: Speakerbox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_fingers)

        speakerbox = Speakerbox(application)
        speakerbox.enableVolumeControl(this)
        setProgressDialog()

        val fingerObserver = Observable.defer {
            Observable.just(Fingerprint.getFingerPrint(application, this))
                    .map { result -> Fingerprint.saveFingerPrint(application, result, this) }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

        btn_capture_right_fingers.setOnClickListener {
            fingerObserver.subscribe(observer)
        }
    }

    override fun showProgressDialog(title: String?, message: String?) {
        this.runOnUiThread { dialog.show() }
        speakerbox.play("Please, Place fingerPrint")
    }

    override fun dismissProgressDialog() {
        this.runOnUiThread { dialog.dismiss() }
    }

    override fun setProgressDialog() {
        dialogBuilder = MaterialDialog.Builder(this)
                .title(R.string.enrol)
                .content(R.string.press_finger)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
        dialog = dialogBuilder.build()
    }

    override fun showInfoToast(info: String?) {
        this.runOnUiThread { Toast.makeText(this@CaptureFingers, info, Toast.LENGTH_SHORT).show() }
    }

    private val observer = object : Observer<Int> {
        override fun onNext(id: Int) {
            if (id >= 0) {
                fingerIDMap.put(fingerCount, id)
                speakerbox.play("Fingerprint gotten")
                //TODO continue from here ..start activity for result when done

            } else {

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

        }

    }

}
