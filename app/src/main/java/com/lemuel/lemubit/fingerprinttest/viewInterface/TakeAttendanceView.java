package com.lemuel.lemubit.fingerprinttest.viewInterface;

public interface TakeAttendanceView {
    void onUpdateInfoTextView(String info);
    void onPlayNotificationSound(int res);
    void onInfoGotten(int ID, String name, String lastName);
}
