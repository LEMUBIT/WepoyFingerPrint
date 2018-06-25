package com.lemuel.lemubit.fingerprinttest.presenter

import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.model.RealmModel
import com.lemuel.lemubit.fingerprinttest.viewInterface.TakeAttendanceView

class TakeAttendancePresenter {

    fun getUserInfo(ID: Int, takeAttendanceView: TakeAttendanceView) {
        val realmModel = RealmModel()

        takeAttendanceView.onUpdateInfoTextView(realmModel.getUserInfo(ID).name)
    }

    fun playSound(state: Int, takeAttendanceView: TakeAttendanceView) {

        when (state) {
            GOOD -> takeAttendanceView.onPlayNotificationSound(R.raw.good)
        }

    }

    companion object {

        val GOOD = 0
        val BAD=1
    }
}
