package com.lemuel.lemubit.fingerprinttest.viewInterface;

public interface FingerPrintInterface {

    void showProgressDialog(String title, String message);

    void dismissProgressDialog();

    void setProgressDialog();

    void showInfoToast(String info);
}
