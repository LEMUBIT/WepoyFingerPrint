package com.lemuel.lemubit.fingerprinttest.operations;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lemuel.lemubit.fingerprinttest.R;
import com.wepoy.fp.FingerprintImage;

public class GetBitMap {
    public static Bitmap bitmap(Application context, FingerprintImage fi)
    {
        byte[] fpBmp = null;
        Bitmap bitmap;
        if (fi == null || (fpBmp = fi.convert2Bmp()) == null || (bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length)) == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.nofinger);
        }

        return bitmap;
    }
}
