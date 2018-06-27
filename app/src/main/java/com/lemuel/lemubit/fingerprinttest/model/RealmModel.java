package com.lemuel.lemubit.fingerprinttest.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class RealmModel extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private String lastName;

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String registerNewUser(int ID, String name, String lastName) {
        String status;
        try {
            RealmModel user = new RealmModel();
            user.setId(ID);
            user.setName(name);
            user.setLastName(lastName);
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
