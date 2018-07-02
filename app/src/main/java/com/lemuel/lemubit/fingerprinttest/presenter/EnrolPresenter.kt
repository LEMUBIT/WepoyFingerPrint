package com.lemuel.lemubit.fingerprinttest.presenter

import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.model.DataHelper
import com.lemuel.lemubit.fingerprinttest.viewInterface.EnrolActivityView

object EnrolPresenter {
    const val GOOD = 0
    const val BAD = 1

    fun registerNewUserInLocalDB(ID: Int, name: String, lastName: String): String {
        return DataHelper.registerNewUser(ID, name, lastName )
    }


    fun playSound(state: Int, enrolActivityView: EnrolActivityView) {

        when (state) {
            GOOD -> enrolActivityView. onPlayNotificationSound(R.raw.great)
            BAD -> enrolActivityView.onPlayNotificationSound(R.raw.bad)
        }

    }

}