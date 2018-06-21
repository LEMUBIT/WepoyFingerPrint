package com.lemuel.lemubit.fingerprinttest.presenter;

import android.content.Context;

import com.lemuel.lemubit.fingerprinttest.R;
import com.lemuel.lemubit.fingerprinttest.viewInterface.HomeMenuView;
import com.wepoy.fp.Bione;

public class HomeMenuPresenter {
    HomeMenuView homeMenuView;
    Context context;

    public HomeMenuPresenter(Context context, HomeMenuView homeMenuView) {
        this.homeMenuView = homeMenuView;
        this.context=context;
    }

    public void clearDatabase() {
        int error = Bione.clear();

        if (error == Bione.RESULT_OK) {
            homeMenuView.onDatabaseCleared(context.getString(R.string.clear_fingerprint_database_success));
        } else {
            homeMenuView.onDatabaseCleared(context.getString(R.string.clear_fingerprint_database_failed));
        }

    }
}
