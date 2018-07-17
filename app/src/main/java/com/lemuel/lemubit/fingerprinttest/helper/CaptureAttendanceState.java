package com.lemuel.lemubit.fingerprinttest.helper;

import com.lemuel.lemubit.fingerprinttest.model.DataHelper;

import java.util.ArrayList;
import java.util.List;

public class CaptureAttendanceState {
    private static List<Integer> attendees=new ArrayList<>();

    public Boolean alreadyCaptured(int ID)
    {
        return attendees.contains(getID(ID));
    }

    public void newAttendee(int fingerPrintID)
    {
        attendees.add(getID(fingerPrintID));
    }

    private int getID(int fingerPrintID)
    {
       return DataHelper.getUserInfo(fingerPrintID).getId();
    }


}
