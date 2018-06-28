package com.lemuel.lemubit.fingerprinttest.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class AttendanceParent extends RealmObject {

    private RealmList<AttendanceRealmModel> attendanceRealmModelRealmList;

    public RealmList<AttendanceRealmModel> getAttendanceRealmModelRealmList() {
        return attendanceRealmModelRealmList;
    }
}
