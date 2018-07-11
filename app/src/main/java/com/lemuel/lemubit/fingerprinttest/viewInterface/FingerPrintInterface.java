package com.lemuel.lemubit.fingerprinttest.viewInterface;

import com.wepoy.fp.FingerprintImage;

public interface FingerPrintInterface {

    void showProgressDialog(String title, String message);

    void dismissProgressDialog();

    void setProgressDialog();

    void showInfoToast(String info);

    void updateFingerPrintImage(FingerprintImage fi);
}
