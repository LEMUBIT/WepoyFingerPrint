package com.lemuel.lemubit.fingerprinttest.presenter

import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.model.RealmModel
import com.lemuel.lemubit.fingerprinttest.viewInterface.TakeAttendanceView

object TakeAttendancePresenter {
    val GOOD = 0
    val BAD = 1

    fun getUserInfo(ID: Int, takeAttendanceView: TakeAttendanceView) {
        val realmModel = RealmModel()

        takeAttendanceView.onUpdateInfoTextView(realmModel.getUserInfo(ID).name)
    }

    fun playSound(state: Int, takeAttendanceView: TakeAttendanceView) {

        when (state) {
            GOOD -> takeAttendanceView. onPlayNotificationSound(R.raw.good)
            BAD -> takeAttendanceView.onPlayNotificationSound(R.raw.bad)
        }

    }


}
