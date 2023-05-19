package com.chatspring.bmob_data;

import android.widget.Toast
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener

class workshop : BmobObject() {
    var appname: String? = null
    var appintro: String? = null
    var appprompt: String? = null
    var icon: String? = null

    fun upload_from_center(
        setAppName: String?,
        setAppDescription: String?,
        setAppPrompt: String?,
    ) {
        var card = workshop()
        card.appname = setAppName
        card.appintro = setAppDescription
        card.appprompt = setAppPrompt

        //保存到数据库
        card.save(object : SaveListener<String?>() {
            override fun done(objectId: String?, e: BmobException?) {
                if (e == null) {
                    Toast.makeText(
                        Bmob.getApplicationContext(),
                        "卡片已成功上传至云端",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    println("创建数据失败：" + e.message)
                    Toast.makeText(
                        Bmob.getApplicationContext(),
                        "卡片上传至云端失败",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

    }
}


