package com.lemuel.lemubit.fingerprinttest.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.helper.Fingers
import com.lemuel.lemubit.fingerprinttest.model.DataHelper
import com.lemuel.lemubit.fingerprinttest.presenter.EnrolPresenter
import com.mapzen.speakerbox.Speakerbox
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_enrol.*
import java.io.ByteArrayOutputStream


class EnrolActivity : AppCompatActivity() {
    private var realm: Realm? = null
    private val REQUEST_IMAGE_CAPTURE = 0
    private val REQUEST_RIGHT_FINGERS = 1
    private val REQUEST_LEFR_FINGERS = 2
    private var userImageCaptured = false
    private var rightFingerIDMap = HashMap<Int, Int>()
    private var leftFingerIDMap = HashMap<Int, Int>()
    private lateinit var capturedPhotoByteArray: ByteArray
    private lateinit var speakerbox: Speakerbox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrol)
        speakerbox = Speakerbox(application)
        speakerbox.enableVolumeControl(this)
        realm = Realm.getDefaultInstance()

        btn_right_capture.setOnClickListener { startActivityForResult(Intent(this, EnrolRightFingers::class.java), REQUEST_RIGHT_FINGERS) }

        btn_left_capture.setOnClickListener { startActivityForResult(Intent(this, EnrolLeftFingers::class.java), REQUEST_LEFR_FINGERS) }

        imageView_user_picture.setOnClickListener { takePicture() }

        btn_finish_registration.setOnClickListener { enrolUser() }
    }

    override fun onDestroy() {
        super.onDestroy()
        speakerbox.shutdown()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_RIGHT_FINGERS -> {
                if (data != null) {
                    speakerbox.play(getString(R.string.right_finger_info_gotten))
                    rightFingerIDMap = data.getSerializableExtra(Fingers.FINGER_ID_HASHMAP_BUNDLE) as HashMap<Int, Int>
                }
            }
            REQUEST_LEFR_FINGERS -> {
                if (data != null) {
                    speakerbox.play(getString(R.string.left_finger_info_gotten))
                    leftFingerIDMap = data.getSerializableExtra(Fingers.FINGER_ID_HASHMAP_BUNDLE) as HashMap<Int, Int>
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    val extras = data?.extras
                    val imageBitmap = extras?.get("data") as Bitmap

                    imageView_user_picture.setImageBitmap(imageBitmap)

                    userImageCaptured = true
                    setImageByteArray(imageBitmap)
                }
            }
        }

    }

    /*set the byteArray representation of current photo captured using it's bitmap*/
    private fun setImageByteArray(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        capturedPhotoByteArray = byteArray
    }

    private fun enrolUser() {
        if (rightFingerIDMap.size == Fingers.ALL_FINGERS_CAPTURED && leftFingerIDMap.size == Fingers.ALL_FINGERS_CAPTURED && userImageCaptured) {
            /*Right thumb is used as ID*/
            val result = EnrolPresenter.registerNewUserInLocalDB(
                    rightFingerIDMap.get(Fingers.THUMB),
                    txt_name.text.toString(),
                    txt_surname.text.toString(),
                    leftFingerIDMap.get(Fingers.THUMB),
                    leftFingerIDMap.get(Fingers.INDEX_FINGER),
                    leftFingerIDMap.get(Fingers.MIDDLE_FINGER),
                    leftFingerIDMap.get(Fingers.RING_FINGER),
                    leftFingerIDMap.get(Fingers.PINKY_FINGER),
                    rightFingerIDMap.get(Fingers.THUMB),
                    rightFingerIDMap.get(Fingers.INDEX_FINGER),
                    rightFingerIDMap.get(Fingers.MIDDLE_FINGER),
                    rightFingerIDMap.get(Fingers.RING_FINGER),
                    rightFingerIDMap.get(Fingers.PINKY_FINGER),
                    capturedPhotoByteArray
            )

            if (result.equals(DataHelper.SUCCESS)) {
                speakerbox.play(getString(R.string.registration_successful))
                finish()
            } else {
                speakerbox.play(getString(R.string.registration_failed))
            }

        } else {
            speakerbox.play(getString(R.string.not_done_with_regsitration))
        }
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    companion object

}
