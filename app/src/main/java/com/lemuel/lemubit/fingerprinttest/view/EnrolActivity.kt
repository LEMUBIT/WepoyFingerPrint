package com.lemuel.lemubit.fingerprinttest.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.operations.Fingerprint
import com.lemuel.lemubit.fingerprinttest.presenter.EnrolPresenter
import com.lemuel.lemubit.fingerprinttest.viewInterface.FingerPrintInterface
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_enrol.*


@SuppressLint("SdCardPath", "HandlerLeak")
class EnrolActivity : AppCompatActivity(), FingerPrintInterface {
    private var realm: Realm? = null
    lateinit var enrolPresenter: EnrolPresenter
    lateinit var dialogBuilder: MaterialDialog.Builder
    lateinit var dialog: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrol)


        realm = Realm.getDefaultInstance()

        enrolPresenter = EnrolPresenter(this)

        //?using just one fingerprint for now
        val fingerObserver = Observable.defer {
            Observable.just(Fingerprint.getFingerPrint(application, this))
                    .map { result -> Fingerprint.saveFingerPrint(application, result, this) }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

        btn_capture.setOnClickListener { fingerObserver.subscribe(observer) }

    }

    override fun setProgressDialog() {
        dialogBuilder = MaterialDialog.Builder(this)
                .title("hey testing")
                .content(R.string.enrol)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
        dialog = dialogBuilder.build()

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

    companion object {
    }

    private val observer = object : Observer<Int> {
        override fun onNext(id: Int) {
            showInfoToast(enrolPresenter.registerNewUserInLocalDB(application, id, txt_name.text.toString()))
        }

        override fun onComplete() {
            showInfoToast("Completed")
        }

        override fun onSubscribe(d: Disposable) {
            showInfoToast("subscribed")
        }

        override fun onError(e: Throwable) {
            showInfoToast("Error received: " + e.message)
        }

    }
}
