package com.lemuel.lemubit.fingerprinttest.model;

import com.lemuel.lemubit.fingerprinttest.helper.DateAndTime;
import com.rollbar.android.Rollbar;

import io.realm.Realm;
import io.realm.RealmResults;

public class DataHelper {

    public static String SUCCESS = "success";
    public static String FAILED = "failed";

    public static String markAttendance(int ID, String name, String lastName, String time, String date) throws Exception {
        String status;
        try {
            AttendanceRealmModel newAttendanceRecord = new AttendanceRealmModel();
            newAttendanceRecord.setId(ID);
            newAttendanceRecord.setName(name);
            newAttendanceRecord.setLastName(lastName);
            newAttendanceRecord.setTime(time);
            newAttendanceRecord.setDate(date);
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> realm.insert(newAttendanceRecord));
            status = "Attendance recorded ID= " + String.valueOf(ID);
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            throw new Exception(status);
        }
        return status;

    }

    public static String registerNewUser(int ID, String name, String lastName, int leftThumb, int leftIndex, int leftMiddle, int leftRing, int leftPinky, int rightThumb, int rightIndex, int rightMiddle, int rightRing, int rightPinky, byte[] photo) {
        String status;
        try {
            RealmModel user = new RealmModel();
            /*Right thumb is used as ID*/
            user.setId(ID);
            user.setName(name);
            user.setLastName(lastName);

            user.setLeftThumb(leftThumb);
            user.setLeftIndex(leftIndex);
            user.setLeftMiddle(leftMiddle);
            user.setLeftRing(leftRing);
            user.setLeftPinky(leftPinky);

            user.setRightThumb(rightThumb);
            user.setRightIndex(rightIndex);
            user.setRightMiddle(rightMiddle);
            user.setRightRing(rightRing);
            user.setRightPinky(rightPinky);

            user.setPhoto(photo);

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> realm.copyToRealmOrUpdate(user));


            status = SUCCESS;

        } catch (Exception e) {
            status = FAILED;
        }

        return status;
    }

    public static RealmResults<AttendanceRealmModel> getTodayAttendance() {
        Realm realm = Realm.getDefaultInstance();
        String currentDate = DateAndTime.getCurrentDate();
        return realm.where(AttendanceRealmModel.class).equalTo("date", currentDate).sort("time").findAll();
    }

    public static RealmResults<AttendanceRealmModel> getAttendanceRecord(String date) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(AttendanceRealmModel.class).equalTo("date", date).sort("time").findAll();
    }

    public static RealmResults<AttendanceRealmModel> getAttendanceRecord() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(AttendanceRealmModel.class).sort("time").findAll();
    }

    public static RealmModel getUserInfo(int ID) {
        //todo debug
        Rollbar.instance().debug("User ID=" + ID);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmModel> result = realm.where(RealmModel.class)
                .equalTo("id", ID)
                .or()
                .equalTo("rightThumb", ID)
                .or()
                .equalTo("rightIndex", ID)
                .or()
                .equalTo("rightMiddle", ID)
                .or()
                .equalTo("rightRing", ID)
                .or()
                .equalTo("rightPinky", ID)
                .or()
                .equalTo("leftThumb", ID)
                .or()
                .equalTo("leftIndex", ID)
                .or()
                .equalTo("leftMiddle", ID)
                .or()
                .equalTo("leftRing", ID)
                .or()
                .equalTo("leftPinky", ID)
                .findAll();

        return result.first();
    }
}
