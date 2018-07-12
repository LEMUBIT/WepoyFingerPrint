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
import kotlinx.android.synthetic.main.activity_capture_left_fingers.*

class CaptureLeftFingers : AppCompatActivity(), FingerPrintInterface {
    //maps the finger count to the ID of the fingerprint so <1,44> is for the first finger that has an ID of 44
    var fingerIDMap = HashMap<Int, Int>()

    //monitors fingers that have been captures, therefor limit is 5
    var fingerCount = 1

    lateinit var dialogBuilder: MaterialDialog.Builder
    lateinit var dialog: MaterialDialog
    lateinit var speakerbox: Speakerbox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_left_fingers)

        speakerbox = Speakerbox(application)
        speakerbox.enableVolumeControl(this)
        setProgressDialog()

        val fingerObserver = Observable.defer {
            Observable.just(Fingerprint.getFingerPrint(application, this))
                    .map { result -> Fingerprint.saveFingerPrint(application, result, this) }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

        btn_capture_left_fingers.setOnClickListener {
            if (fingerCount <= 5) {
                playInstruction()
                fingerObserver.subscribe(observer)
            } else {
                speakerbox.play("Please, proceed with registration")
            }
        }

        btn_lefthand_close.setOnClickListener {
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
        speakerbox.play("Please, proceed with left finger registration")
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
        // this.runOnUiThread { Toast.makeText(this@CaptureRightFingers, info, Toast.LENGTH_SHORT).show() }
    }

    override fun updateFingerPrintImage(fi: FingerprintImage?) {
        this.runOnUiThread {
            val bitmap = GetBitMap.bitmap(application, fi)
            image_left_fingerprint.setImageBitmap(bitmap)
        }

    }

    private val observer = object : Observer<Int> {
        override fun onNext(id: Int) {
            if (id >= 0) {
                fingerIDMap.put(fingerCount, id)
                speakerbox.play("Fingerprint gotten")
                when (fingerCount) {
                    1 -> animation_left_thumb.visibility = View.VISIBLE
                    2 -> animation_left_index.visibility = View.VISIBLE
                    3 -> animation_left_middle.visibility = View.VISIBLE
                    4 -> animation_left_ring.visibility = View.VISIBLE
                    5 -> animation_left_pinky.visibility = View.VISIBLE
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
            1 -> speakerbox.play("Please, place left thumb on sensor")
            2 -> speakerbox.play("Please, place left index finger on sensor")
            3 -> speakerbox.play("Please, Place left middle finger on sensor")
            4 -> speakerbox.play("Please, Place left ring finger on sensor")
            5 -> speakerbox.play("Please, Place left pinky finger on sensor")
        }
    }
}
