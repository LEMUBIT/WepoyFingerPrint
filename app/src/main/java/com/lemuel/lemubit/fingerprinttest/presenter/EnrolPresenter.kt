package com.lemuel.lemubit.fingerprinttest.presenter

import android.content.Context
import com.lemuel.lemubit.fingerprinttest.model.RealmModel
import com.lemuel.lemubit.fingerprinttest.viewInterface.FingerPrintInterface

class EnrolPresenter(FingerPrintInterface: FingerPrintInterface) {

    var realmModel = RealmModel()

    init {
        FingerPrintInterface.setProgressDialog()
    }

    fun registerNewUserInLocalDB(context: Context, ID: Int, name: String): String {
        return realmModel.registerNewUser(context, ID, name)
    }



}