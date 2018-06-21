package com.lemuel.lemubit.fingerprinttest.presenter

import android.content.Context
import com.lemuel.lemubit.fingerprinttest.model.RealmModel
import com.lemuel.lemubit.fingerprinttest.viewInterface.EnrolView

class EnrolPresenter(enrolView: EnrolView) {

    var realmModel = RealmModel()

    init {
        enrolView.setProgressDialog()
    }

    fun registerNewUser(context: Context, ID: Int, name: String): String {
        return realmModel.registerNewUser(context, ID, name)
    }

}