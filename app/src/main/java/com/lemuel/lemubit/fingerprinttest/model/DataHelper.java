package com.lemuel.lemubit.fingerprinttest.model;

import io.realm.Realm;

public class DataHelper {
    public static String markAttendance(int ID, String name, String lastName, String time, String date) {
        String status;
        try {
            AttendanceRealmModel newAttendanceRecord = new AttendanceRealmModel();
            newAttendanceRecord.setId(ID);
            newAttendanceRecord.setName(name);
            newAttendanceRecord.setLastName(lastName);
            newAttendanceRecord.setTime(time);
            newAttendanceRecord.setDate(date);
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> realm.copyToRealmOrUpdate(newAttendanceRecord));
            status = "Attendance recorded ID= " + String.valueOf(ID);
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
        }
        return status;
    }

    public static String registerNewUser(int ID, String name, String lastName) {
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
}
