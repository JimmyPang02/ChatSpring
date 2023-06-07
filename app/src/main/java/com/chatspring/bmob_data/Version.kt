package com.chatspring.bmob_data

import cn.bmob.v3.BmobObject

data class Version(
    val version: String,
    val content: String
) : BmobObject()