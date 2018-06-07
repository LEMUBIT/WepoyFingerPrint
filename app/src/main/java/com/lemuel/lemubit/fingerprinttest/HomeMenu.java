package com.lemuel.lemubit.fingerprinttest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bugsnag.android.Bugsnag;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class HomeMenu extends AppCompatActivity {
    @BindView(R.id.btn_enrol)
    Button enrollBtn;
    @BindView(R.id.btn_takeAttendance)
    Button takeAttendanceBtn;
    @BindView(R.id.btn_viewAttendance)
    Button viewAttendanceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
        ButterKnife.bind(this);
        // Initialize Realm (just once per application)
        Realm.init(getApplicationContext());
        //Initialize Bugsnag to track crash
        Bugsnag.init(this);
        enrollBtn.setOnClickListener(v -> {
            startActivity(new Intent(HomeMenu.this, EnrolActivity.class));
        });

        //todo used to test the RxJava
      //  Observable.just(showIT()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(Toast::show);
    }


    public Toast showIT()
    {
      return Toast.makeText(HomeMenu.this, "RxJava", Toast.LENGTH_SHORT);
    }
}
