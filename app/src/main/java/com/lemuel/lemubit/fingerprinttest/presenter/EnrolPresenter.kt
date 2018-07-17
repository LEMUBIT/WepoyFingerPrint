package com.lemuel.lemubit.fingerprinttest.presenter

import com.lemuel.lemubit.fingerprinttest.model.DataHelper

object EnrolPresenter {

    fun registerNewUserInLocalDB(
            ID: Int?,
            name: String,
            lastName: String,
            leftThumb: Int?,
            leftIndex: Int?,
            leftMiddle: Int?,
            leftRing: Int?,
            leftPinky: Int?,
            rightThumb: Int?,
            rightIndex: Int?,
            rightMiddle: Int?,
            rightRing: Int?,
            rightPinky: Int?,
            photo: ByteArray
    ): String {
        return DataHelper.registerNewUser(
                ID!!,
                name,
                lastName,
                leftThumb!!,
                leftIndex!!,
                leftMiddle!!,
                leftRing!!,
                leftPinky!!,
                rightThumb!!,
                rightIndex!!,
                rightMiddle!!,
                rightRing!!,
                rightPinky!!,
                photo
        )
    }

}