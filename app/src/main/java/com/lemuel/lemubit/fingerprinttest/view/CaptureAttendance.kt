package com.lemuel.lemubit.fingerprinttest.view

import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.helper.CaptureAttendanceState
import com.lemuel.lemubit.fingerprinttest.helper.CaptureAttendanceState.attendees
import com.lemuel.lemubit.fingerprinttest.helper.DateAndTime
import com.lemuel.lemubit.fingerprinttest.model.DataHelper
import com.lemuel.lemubit.fingerprinttest.operations.Fingerprint
import com.lemuel.lemubit.fingerprinttest.presenter.TakeAttendancePresenter
import com.lemuel.lemubit.fingerprinttest.recyclerview.CaptureAttendanceAdapter
import com.lemuel.lemubit.fingerprinttest.viewInterface.FingerPrintInterface
import com.lemuel.lemubit.fingerprinttest.viewInterface.TakeAttendanceView
import com.rollbar.android.Rollbar
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_capture_attendance.*


class CaptureAttendance : AppCompatActivity(), TakeAttendanceView, FingerPrintInterface {
    lateinit var mPlayer: MediaPlayer
    var mPlayerInitialized = false
    private var recyclerView: RecyclerView? = null
    private var adapter: CaptureAttendanceAdapter? = null
    lateinit var dialogBuilder: MaterialDialog.Builder
    lateinit var dialog: MaterialDialog
    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_attendance)
        realm = Realm.getDefaultInstance()
        recyclerView = findViewById(R.id.recyclerv_attendee)
        setProgressDialog()

        /**RxJava operation gets fingerPrint Data from getFingerPrint() and Maps the result to getUserID which calls
         * the observer's onNext method and sends the userID with the notification.
         */
        val fingerObserver = Observable.defer {
            Observable.just(Fingerprint.getFingerPrint(application, this))
                    .map { result -> Fingerprint.getUserID(result) }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())


        lottie_fingerprint.setOnClickListener {
            fingerObserver.subscribe(observer)
        }


        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        adapter = CaptureAttendanceAdapter(DataHelper.getTodayAttendance(), this)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
        recyclerView?.setHasFixedSize(true)
        recyclerView?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayerInitialized) {
            mPlayer.release()
        }
    }

    override fun onUpdateInfoTextView(info: String?) {
        txt_currentTime.text = info
    }

    override fun onPlayNotificationSound(res: Int) {
        mPlayer = MediaPlayer.create(this@CaptureAttendance, res)
        mPlayer.start()
        mPlayerInitialized = true
    }

    override fun onInfoGotten(ID: Int, name: String, lastName: String) {
        DataHelper.markAttendance(ID, name, lastName, DateAndTime.getCurrentTime(), DateAndTime.getCurrentDate())
        Rollbar.instance().log("Info Gotten:" + ID.toString())

        adapter?.notifyDataSetChanged()
    }

    override fun setProgressDialog() {
        dialogBuilder = MaterialDialog.Builder(this)
                .title("Take your attendance")
                .content(R.string.press_finger)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
        dialog = dialogBuilder.build()
    }

    override fun showProgressDialog(title: String?, message: String?) {
        this.runOnUiThread { dialog.show() }
    }

    override fun dismissProgressDialog() {
        this.runOnUiThread { dialog.dismiss() }
    }

    override fun showInfoToast(info: String?) {
        this.runOnUiThread { Toast.makeText(this@CaptureAttendance, info, Toast.LENGTH_SHORT).show() }
    }

    private val observer = object : Observer<Int?> {
        override fun onNext(id: Int) {
            if (id >= 0 && !CaptureAttendanceState.alreadyCaptured(id)) {
                TakeAttendancePresenter.getUserInfo(id, this@CaptureAttendance)
                TakeAttendancePresenter.playSound(TakeAttendancePresenter.GOOD, this@CaptureAttendance)
                attendees.add(id)
            } else {
                if (CaptureAttendanceState.alreadyCaptured(id))
                    showInfoToast("You're already captured for Today")

                TakeAttendancePresenter.playSound(TakeAttendancePresenter.BAD, this@CaptureAttendance)
            }
        }

        override fun onComplete() {
            Log.d("RxJAVA:", "completed")
        }

        override fun onSubscribe(d: Disposable) {
            Log.d("RxJAVA:", "subscribed")
        }

        override fun onError(e: Throwable) {
            TakeAttendancePresenter.playSound(TakeAttendancePresenter.BAD, this@CaptureAttendance)
            Log.d("RxJAVA:", e.message)
        }

    }


}
