package com.lemuel.lemubit.fingerprinttest.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.helper.Fingers
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
    var currentFinger = Fingers.THUMB
    private lateinit var dialogBuilder: MaterialDialog.Builder
    private lateinit var dialog: MaterialDialog
    lateinit var speakerbox: Speakerbox
    private var allFingersCaptured = false
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
            if (!allFingersCaptured) {
                playInstruction()
                fingerObserver.subscribe(observer)
            } else {
                speakerbox.play(getString(R.string.proceed_with_registration))
            }
        }

        btn_righthand_close.setOnClickListener {
            if (allFingersCaptured) {
                val intent = Intent()
                val mBundle = Bundle()
                mBundle.putSerializable(Fingers.FINGER_ID_HASHMAP_BUNDLE, fingerIDMap)
                intent.putExtras(mBundle)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                speakerbox.play(getString(R.string.registration_canceled))
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        speakerbox.play(getString(R.string.proceed_with_right_finger_registration))
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
                fingerIDMap.put(currentFinger, id)
                speakerbox.play(getString(R.string.fingerprint_gotten))
                when (currentFinger) {
                    Fingers.THUMB -> animation_right_thumb.visibility = View.VISIBLE
                    Fingers.INDEX_FINGER -> animation_right_index.visibility = View.VISIBLE
                    Fingers.MIDDLE_FINGER -> animation_right_middle.visibility = View.VISIBLE
                    Fingers.RING_FINGER -> animation_right_ring.visibility = View.VISIBLE
                    Fingers.PINKY_FINGER -> animation_right_pinky.visibility = View.VISIBLE
                }
                nextFinger()
            } else {
                speakerbox.play(getString(R.string.try_again))
            }
        }

        override fun onComplete() {
            Log.d("RxJAVA:", "completed")
        }

        override fun onSubscribe(d: Disposable) {
            Log.d("RxJAVA:", "subscribed")
        }

        override fun onError(e: Throwable) {
            speakerbox.play(getString(R.string.try_again))
        }

    }

    private fun nextFinger() {
        if (currentFinger < Fingers.PINKY_FINGER) {
            currentFinger++
        } else {
            allFingersCaptured = true
        }
    }

    private fun playInstruction() {
        when (currentFinger) {
            Fingers.THUMB -> speakerbox.play(getString(R.string.place_right_thumb))
            Fingers.INDEX_FINGER -> speakerbox.play(getString(R.string.place_right_index))
            Fingers.MIDDLE_FINGER -> speakerbox.play(getString(R.string.place_right_middle))
            Fingers.RING_FINGER -> speakerbox.play(getString(R.string.place_right_ring))
            Fingers.PINKY_FINGER -> speakerbox.play(getString(R.string.place_right_pinky))
        }
    }

}
