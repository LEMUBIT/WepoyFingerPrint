package com.lemuel.lemubit.fingerprinttest.presenter;

import com.lemuel.lemubit.fingerprinttest.model.RealmModel;
import com.lemuel.lemubit.fingerprinttest.viewInterface.TakeAttendanceView;

public class TakeAttendancePresenter {

    public void getUserInfo(int ID, TakeAttendanceView takeAttendanceView)
    {
        RealmModel realmModel=new RealmModel();

        takeAttendanceView.onUpdateInfoTextView(realmModel.getUserInfo(ID).getName());
    }
}
