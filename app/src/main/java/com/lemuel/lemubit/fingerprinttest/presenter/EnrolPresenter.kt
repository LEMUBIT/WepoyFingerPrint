package com.lemuel.lemubit.fingerprinttest.presenter

import android.content.Context
import com.lemuel.lemubit.fingerprinttest.model.RealmModel
import com.lemuel.lemubit.fingerprinttest.model.modelInterface.fingerPrintInterface

class EnrolPresenter(fingerPrintInterface: fingerPrintInterface) {

    var realmModel = RealmModel()

    init {
        fingerPrintInterface.setProgressDialog()
    }

    fun registerNewUserInLocalDB(context: Context, ID: Int, name: String): String {
        return realmModel.registerNewUser(context, ID, name)
    }



}