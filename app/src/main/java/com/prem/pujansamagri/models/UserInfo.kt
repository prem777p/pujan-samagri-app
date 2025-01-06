package com.prem.pujansamagri.models

import java.io.Serializable


data class UserInfo (
    var name: String,
    var phoneNo: String,
    var title: String
): Serializable{
    constructor(userInfo: UserInfo):this(userInfo.name, userInfo.phoneNo, userInfo.title)
}


