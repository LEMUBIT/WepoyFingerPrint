package com.lemuel.lemubit.fingerprinttest.presenter

import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.model.DataHelper
import com.lemuel.lemubit.fingerprinttest.viewInterface.TakeAttendanceView

object TakeAttendancePresenter {
    const val GOOD = 0
    const val BAD = 1

    fun getUserInfo(ID: Int, takeAttendanceView: TakeAttendanceView) {
        takeAttendanceView.onUpdateInfoTextView(DataHelper.getUserInfo(ID).name)
        takeAttendanceView.onInfoGotten(ID, DataHelper.getUserInfo(ID).name, DataHelper.getUserInfo(ID).lastName)
    }

    fun playSound(state: Int, takeAttendanceView: TakeAttendanceView) {

        when (state) {
            GOOD -> takeAttendanceView.onPlayNotificationSound(R.raw.great)
            BAD -> takeAttendanceView.onPlayNotificationSound(R.raw.bad)
        }

    }


}
