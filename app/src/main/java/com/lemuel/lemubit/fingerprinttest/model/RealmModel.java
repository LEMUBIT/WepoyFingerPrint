package com.lemuel.lemubit.fingerprinttest.model;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class RealmModel extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String registerNewUser(Context context, int ID, String name) {
        String status;
        try {
            RealmModel user = new RealmModel();
            user.setId(ID);
            user.setName(name);
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> realm.copyToRealmOrUpdate(user));
            status = "User saved ID= " + String.valueOf(ID);
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
        }

        return status;
    }

    public RealmModel getUserInfo(int ID)
    {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmModel> result = realm.where(RealmModel.class)
                .equalTo("id", ID)
                .findAll();

        return result.first();
    }
}
