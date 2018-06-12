package com.lemuel.lemubit.fingerprinttest

import com.wepoy.util.Result

class FingerPrintResponse(var res: Result?, var state: Int) {
    companion object {
        val FINGER_RESPONSE_GOOD = 1
        val FINGER_RESPONSE_BAD = 0
    }
}
