package com.lemuel.lemubit.fingerprinttest.presenter

import com.lemuel.lemubit.fingerprinttest.R
import com.lemuel.lemubit.fingerprinttest.model.DataHelper
import com.lemuel.lemubit.fingerprinttest.viewInterface.TakeAttendanceView

object TakeAttendancePresenter {
    const val GOOD = 0
    const val BAD = 1

    fun getUserInfo(fingerPrintID: Int, takeAttendanceView: TakeAttendanceView) {
        takeAttendanceView.onUpdateInfoTextView(DataHelper.getUserInfo(fingerPrintID).name)
        takeAttendanceView.onInfoGotten(DataHelper.getUserInfo(fingerPrintID).getId(), DataHelper.getUserInfo(fingerPrintID).name, DataHelper.getUserInfo(fingerPrintID).lastName)
    }

    fun playSound(state: Int, takeAttendanceView: TakeAttendanceView) {

        when (state) {
            GOOD -> takeAttendanceView.onPlayNotificationSound(R.raw.great)
            BAD -> takeAttendanceView.onPlayNotificationSound(R.raw.bad)
        }

    }


}
