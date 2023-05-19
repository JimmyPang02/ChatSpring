package com.chatspring.bmob_data

import cn.bmob.v3.BmobUser

class MyUser: BmobUser() {
    var nickname: String? = null
        get() = field
        set(value) {
            field = value
        }
}
