package com.lemuel.lemubit.fingerprinttest.view

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.model.Fingerprint
import com.lemuel.lemubit.fingerprinttest.presenter.TakeAttendancePresenter
import com.lemuel.lemubit.fingerprinttest.viewInterface.FingerPrintInterface
import com.lemuel.lemubit.fingerprinttest.viewInterface.TakeAttendanceView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_take_attendance.*

class TakeAttendance : AppCompatActivity(), FingerPrintInterface, TakeAttendanceView {
    private var mProgressDialog: ProgressDialog? = null
    lateinit var takeAttendancePresenter:TakeAttendancePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_attendance)

        takeAttendancePresenter= TakeAttendancePresenter()

        val fingerObserver = Observable.defer {
            Observable.just(Fingerprint.getFingerPrint(application, this))
                    .map { result -> Fingerprint.getUserID(result) }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

        lottie_fingerprint.setOnClickListener {
            fingerObserver.subscribe(observer)
        }
    }

    override fun onUpdateInfoTextView(info: String?) {
        txt_currentTime.setText(info)
    }

    override fun showProgressDialog(title: String?, message: String?) {
        // Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.d("Hey!", message)
    }

    override fun dismissProgressDialog()  {
        Log.d("HEY!", "Dismiss progressDialog")
    }

    override fun setProgressDialog() {
        Log.d("HEY!", "Set progressDialog")
    }

    override fun showInfoToast(info: String?) {
        Log.d("HEY!", info)
    }

    private val observer = object : Observer<Int?> {
        override fun onNext(id: Int) {
           takeAttendancePresenter.getUserInfo(id,this@TakeAttendance)
        }

        override fun onComplete() {
            Log.d("HEY!", "Completed")
        }

        override fun onSubscribe(d: Disposable) {
            Log.d("Hey!", "subscribed")
        }


        //!TODO stopped and used text to check error, toast not able to show from RxJava. Sort out sending of only important msg to user
        override fun onError(e: Throwable) {
            txt_currentTime.setText(e.message)
        }

    }

}
