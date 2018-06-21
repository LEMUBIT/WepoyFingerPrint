package com.lemuel.lemubit.fingerprinttest.viewInterface;

public interface EnrolView {

    void showProgressDialog(String title, String message);

    void dismissProgressDialog();

    void setProgressDialog();

    void showInfoToast(String info);
}
