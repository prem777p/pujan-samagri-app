package com.prem.pujansamagri.`interface`

import com.prem.pujansamagri.models.Item
import com.prem.pujansamagri.models.UserInfo

interface LocalParcheStorage {

    fun initializePreDefineParche()
    fun savedParcha(fileName: String, userInfo: UserInfo, itemList: ArrayList<Item>?): Boolean
    fun parcheExist(dir: String): Boolean
}