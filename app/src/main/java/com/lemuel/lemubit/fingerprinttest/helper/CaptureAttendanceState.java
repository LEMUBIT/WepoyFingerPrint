package com.lemuel.lemubit.fingerprinttest.helper;

import java.util.ArrayList;
import java.util.List;

public class CaptureAttendanceState {
    public static List<Integer> attendees=new ArrayList<>();

    public static Boolean alreadyCaptured(int ID)
    {
        return attendees.contains(ID);
    }
}
