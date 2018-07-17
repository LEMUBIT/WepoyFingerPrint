package com.lemuel.lemubit.fingerprinttest.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.operations.Fingerprint
import com.lemuel.lemubit.fingerprinttest.operations.GetBitMap
import com.lemuel.lemubit.fingerprinttest.viewInterface.FingerPrintInterface
import com.mapzen.speakerbox.Speakerbox
import com.wepoy.fp.FingerprintImage
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_capture_right_fingers.*


class EnrolRightFingers : AppCompatActivity(), FingerPrintInterface {

    var fingerIDMap = HashMap<Int, Int>()
    var fingerCount = 1
    lateinit var dialogBuilder: MaterialDialog.Builder
    lateinit var dialog: MaterialDialog
    lateinit var speakerbox: Speakerbox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_right_fingers)

        speakerbox = Speakerbox(application)
        speakerbox.enableVolumeControl(this)
        setProgressDialog()

        val fingerObserver = Observable.defer {
            Observable.just(Fingerprint.getFingerPrint(application, this))
                    .map { result -> Fingerprint.saveFingerPrint(application, result, this) }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

        btn_capture_right_fingers.setOnClickListener {
            if (fingerCount <= 5) {
                playInstruction()
                fingerObserver.subscribe(observer)
            } else {
                speakerbox.play("Please, proceed with registration")
            }
        }

        btn_righthand_close.setOnClickListener {
            if (fingerCount >= 5) {
                val intent = Intent()
                val mBundle = Bundle()
                mBundle.putSerializable("FingerIDHashMap", fingerIDMap)
                intent.putExtras(mBundle)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                speakerbox.play("Registration canceled!")
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        speakerbox.play("Please, proceed with right finger registration")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun showProgressDialog(title: String?, message: String?) {
        this.runOnUiThread { dialog.show() }
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
        // this.runOnUiThread { Toast.makeText(this@EnrolRightFingers, info, Toast.LENGTH_SHORT).show() }
    }

    override fun updateFingerPrintImage(fi: FingerprintImage?) {
        this.runOnUiThread {
            val bitmap = GetBitMap.bitmap(application, fi)
            image_right_fingerprint.setImageBitmap(bitmap)
        }

    }

    private val observer = object : Observer<Int> {
        override fun onNext(id: Int) {
            if (id >= 0) {
                fingerIDMap.put(fingerCount, id)
                speakerbox.play("Fingerprint gotten")
                when (fingerCount) {
                    1 -> animation_right_thumb.visibility = View.VISIBLE
                    2 -> animation_right_index.visibility = View.VISIBLE
                    3 -> animation_right_middle.visibility = View.VISIBLE
                    4 -> animation_right_ring.visibility = View.VISIBLE
                    5 -> animation_right_pinky.visibility = View.VISIBLE
                }
                fingerCount++
            } else {
                speakerbox.play("Sorry, please try again")
            }
        }

        override fun onComplete() {
            Log.d("RxJAVA:", "completed")
        }

        override fun onSubscribe(d: Disposable) {
            Log.d("RxJAVA:", "subscribed")
        }

        override fun onError(e: Throwable) {
            speakerbox.play("Sorry, please try again")
        }

    }

    private fun playInstruction() {
        when (fingerCount) {
            1 -> speakerbox.play("Please, place right thumb on sensor")
            2 -> speakerbox.play("Please, place right index finger on sensor")
            3 -> speakerbox.play("Please, Place right middle finger on sensor")
            4 -> speakerbox.play("Please, Place right ring finger on sensor")
            5 -> speakerbox.play("Please, Place right pinky finger on sensor")
        }
    }

}
