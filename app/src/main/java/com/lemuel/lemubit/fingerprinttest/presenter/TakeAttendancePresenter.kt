package com.lemuel.lemubit.fingerprinttest.presenter

import com.lemuel.lemubit.fingerprinttest.model.DataHelper
import com.lemuel.lemubit.fingerprinttest.viewInterface.TakeAttendanceView

object TakeAttendancePresenter {

    fun getUserInfo(fingerPrintID: Int, takeAttendanceView: TakeAttendanceView) {
        takeAttendanceView.onUpdateInfoTextView(DataHelper.getUserInfo(fingerPrintID).name)
        takeAttendanceView.onInfoGotten(DataHelper.getUserInfo(fingerPrintID).getId(), DataHelper.getUserInfo(fingerPrintID).name, DataHelper.getUserInfo(fingerPrintID).lastName)
    }

}
