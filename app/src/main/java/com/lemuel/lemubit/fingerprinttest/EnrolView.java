package com.lemuel.lemubit.fingerprinttest;

public interface EnrolView {
   void showProgressDialog(String title, String message);
    void dismissProgressDialog();
    void showInfoToast(String info);
}
