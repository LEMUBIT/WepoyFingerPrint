package com.lemuel.lemubit.fingerprinttest.presenter

import android.content.Context
import com.lemuel.lemubit.fingerprinttest.model.RealmModel

class EnrolPresenter {
    var realmModel = RealmModel()

    fun registerNewUser(context: Context, ID: Int, name: String):String {
        return realmModel.registerNewUser(context, ID, name)
    }


}