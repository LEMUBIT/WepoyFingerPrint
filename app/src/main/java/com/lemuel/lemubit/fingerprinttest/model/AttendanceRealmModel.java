package com.lemuel.lemubit.fingerprinttest.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AttendanceRealmModel extends RealmObject {
    @PrimaryKey
    private int id;

    private String name;

    private String lastName;

    private String time;

    private String date;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String markAttendance(int ID, String name, String lastName, String time, String date) {
        String status;
        try {
            AttendanceRealmModel newAttendanceRecord = new AttendanceRealmModel();
            newAttendanceRecord.id = ID;
            newAttendanceRecord.name = name;
            newAttendanceRecord.lastName = lastName;
            newAttendanceRecord.time = time;
            newAttendanceRecord.date = date;
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> realm.copyToRealmOrUpdate(newAttendanceRecord));
            status = "Attendance recorded ID= " + String.valueOf(ID);
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
        }
        return status;
    }
}
