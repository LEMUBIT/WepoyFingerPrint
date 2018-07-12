package com.lemuel.lemubit.fingerprinttest.presenter

import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.model.DataHelper
import com.lemuel.lemubit.fingerprinttest.viewInterface.EnrolActivityView

object EnrolPresenter {
    const val GOOD = 0
    const val BAD = 1

    fun registerNewUserInLocalDB(
            ID: Int,
            name: String,
            lastName: String,
            leftThumb: Int,
            leftIndex: Int,
            leftMiddle: Int,
            leftRing: Int,
            leftPinky: Int,
            rightThumb: Int,
            rightIndex: Int,
            rightMiddle: Int,
            rightRing: Int,
            rightPinky: Int
    ): String {
        return DataHelper.registerNewUser(ID, name, lastName, leftThumb, leftIndex, leftMiddle, leftRing, leftPinky, rightThumb, rightIndex, rightMiddle, rightRing, rightPinky)
    }

    fun playSound(state: Int, enrolActivityView: EnrolActivityView) {

        when (state) {
            GOOD -> enrolActivityView.onPlayNotificationSound(R.raw.great)
            BAD -> enrolActivityView.onPlayNotificationSound(R.raw.bad)
        }

    }

}