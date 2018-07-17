package com.lemuel.lemubit.fingerprinttest.view

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.model.DataHelper
import com.lemuel.lemubit.fingerprinttest.presenter.EnrolPresenter
import com.lemuel.lemubit.fingerprinttest.viewInterface.EnrolActivityView
import com.mapzen.speakerbox.Speakerbox
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_enrol.*
import java.io.ByteArrayOutputStream


class EnrolActivity : AppCompatActivity(), EnrolActivityView {
    private var realm: Realm? = null
    lateinit var dialogBuilder: MaterialDialog.Builder
    lateinit var dialog: MaterialDialog
    lateinit var mPlayer: MediaPlayer

    val REQUEST_IMAGE_CAPTURE = 0
    val REQUEST_RIGHT_FINGERS = 1
    val REQUEST_LEFR_FINGERS = 2
    var mImageCaptured = false
    var mPlayerInitialized = false
    var rightFingerIDMap = HashMap<Int, Int>()
    var leftFingerIDMap = HashMap<Int, Int>()
    lateinit var capturedPhotoByteArray: ByteArray

    lateinit var speakerbox: Speakerbox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrol)

        setProgressDialog()
        speakerbox = Speakerbox(application)
        speakerbox.enableVolumeControl(this)
        realm = Realm.getDefaultInstance()


        btn_right_capture.setOnClickListener { startActivityForResult(Intent(this, EnrolRightFingers::class.java), REQUEST_RIGHT_FINGERS) }

        btn_left_capture.setOnClickListener { startActivityForResult(Intent(this, EnrolLeftFingers::class.java), REQUEST_LEFR_FINGERS) }

        imageView_user_picture.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

        btn_finish_registration.setOnClickListener {
            if (rightFingerIDMap.size == 5 && leftFingerIDMap.size == 5 && mImageCaptured) {
                /*Right thumb is used as ID*/
                val result = EnrolPresenter.registerNewUserInLocalDB(
                        rightFingerIDMap.get(1),
                        txt_name.text.toString(),
                        txt_surname.text.toString(),
                        leftFingerIDMap.get(1),
                        leftFingerIDMap.get(2),
                        leftFingerIDMap.get(3),
                        leftFingerIDMap.get(4),
                        leftFingerIDMap.get(5),
                        rightFingerIDMap.get(1),
                        rightFingerIDMap.get(2),
                        rightFingerIDMap.get(3),
                        rightFingerIDMap.get(4),
                        rightFingerIDMap.get(5),
                        capturedPhotoByteArray
                )

                if (result.equals(DataHelper.SUCCESS)) {
                    speakerbox.play("Registration successful")
                    finish()
                } else {
                    speakerbox.play("Sorry, registration failed")
                }

            } else {
                speakerbox.play("Sorry, you are not done with registration")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayerInitialized) {
            mPlayer.release()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_RIGHT_FINGERS -> {
                if (data != null) {
                    speakerbox.play("Right finger information gotten")
                    rightFingerIDMap = data.getSerializableExtra("FingerIDHashMap") as HashMap<Int, Int>
                }
            }
            REQUEST_LEFR_FINGERS -> {
                if (data != null) {
                    speakerbox.play("Left finger information gotten")
                    leftFingerIDMap = data.getSerializableExtra("FingerIDHashMap") as HashMap<Int, Int>
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    val extras = data?.extras
                    val imageBitmap = extras?.get("data") as Bitmap

                    imageView_user_picture.setImageBitmap(imageBitmap)
                    mImageCaptured = true

                    val stream = ByteArrayOutputStream()
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    setImageByteArray(byteArray)
                }
            }
        }

    }

    /*set the byteArray representation of current photo captured*/
    private fun setImageByteArray(byteArray: ByteArray) {
        capturedPhotoByteArray = byteArray
    }

    private fun setProgressDialog() {
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

    companion object

}
