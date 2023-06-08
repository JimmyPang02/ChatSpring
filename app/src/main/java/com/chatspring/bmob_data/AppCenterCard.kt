package com.chatspring.bmob_data

import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.bmob.v3.Bmob.getApplicationContext
import cn.bmob.v3.BmobObject
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.chatspring.GlobaluserName
import com.chatspring.bmob_mObjectId_list
import com.chatspring.bmob_model_list


class AppCenterCard : BmobObject() {

    var appName: String? = null
    var appDescription: String? = null
    var appPrompt: String? = null
    var userName: String? = null
    var mObjectId: String? = null
    var icon: String? = null

    //添加数据
    fun create_card(
        setAppName: String?,
        setAppDescription: String?,
        setAppPrompt: String?,
        setUserName: String?,
        seticon: String?
    ) {
        val card = AppCenterCard()
        card.appName = setAppName
        card.appDescription = setAppDescription
        card.appPrompt = setAppPrompt
        card.userName = setUserName
        card.icon = seticon


        //保存到数据库
        card.save(object : SaveListener<String?>() {
            override fun done(objectId: String?, e: BmobException?) {
                if (e == null) {
                    mObjectId = objectId
                    //存入数组mObjectId_list
                    //mObjectId?.let { bmob_mObjectId_list.add(it) }
                    //存入bmob_model_list
                    //bmob_model_list.add(card)
//                    Toast.makeText(
//                        getApplicationContext(),
//                        "卡片已成功上传至云端",
//                        Toast.LENGTH_SHORT
//                    ).show()
                } else {
                    println("创建数据失败：" + e.message)
//                    Toast.makeText(
//                        getApplicationContext(),
//                        "卡片上传至云端失败",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }
        })
    }

    fun get_card(objectId: String?) {
        var bmobQuery: BmobQuery<AppCenterCard> = BmobQuery()
        bmobQuery.getObject(objectId, object : QueryListener<AppCenterCard>() {
            override fun done(onlineCard: AppCenterCard?, ex: BmobException?) {
                if (ex == null) {
                    //存入bmob_model_list
                    onlineCard?.let { bmob_model_list.add(it) }


                    //存入数组mObjectId_list
                    onlineCard?.objectId?.let { bmob_mObjectId_list.add(it) }


                } else {
                    println("查询失败：" + ex.message)
                }
            }
        })
    }

    fun get_all_cards() {
        var bmobQuery: BmobQuery<AppCenterCard> = BmobQuery()
        bmobQuery.addWhereEqualTo("userName", GlobaluserName)
        bmobQuery.findObjects(object : FindListener<AppCenterCard>() {
            override fun done(cards: MutableList<AppCenterCard>?, ex: BmobException?) {

                if (ex == null) {
                    println("查询成功：共" + cards?.size + "条数据。")
                    if (cards != null) {
                        //清空bmob_model_list和bmob_mObjectId_list
                        bmob_model_list.clear()
                        bmob_mObjectId_list.clear()

                        for (card: AppCenterCard in cards) {
                            //检测userName字段是否和GlobaluserName一样
                            if (card.userName == GlobaluserName) {
                                //存入bmob_model_list
                                bmob_model_list.add(card)
                                //存入数组mObjectId_list
                                card.objectId?.let { bmob_mObjectId_list.add(it) }

                                //通知appCenterFragment更新卡片
                                val intent = Intent("com.chatspring.appCenter")
                                intent.putExtra("isGetAllCards", true)
                                LocalBroadcastManager.getInstance(getApplicationContext())
                                    .sendBroadcast(intent)

                            }
                        }
                        //弹出用户名和存入数组的卡片数
                        Toast.makeText(
                            getApplicationContext(),
                            "用户名：" + GlobaluserName + "，卡片数：" + bmob_model_list.size,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    println("查询失败：" + ex.message)
                    Toast.makeText(
                        getApplicationContext(),
                        "查询失败：" + ex.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    fun delete_card(objectId: String?) {
        var card = AppCenterCard()
        card.objectId = objectId
        card.delete(object : UpdateListener() {
            override fun done(ex: BmobException?) {
                if (ex == null) {
                    println("删除成功")
                    Toast.makeText(getApplicationContext(), "卡片删除成功", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    println("删除失败")
                    Toast.makeText(getApplicationContext(), "卡片删除失败", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    fun upgrade_card(
        objectId: String?,
        setAppName: String?,
        setAppDescription: String?,
        setAppPrompt: String?
    ) {
        var card = AppCenterCard()
        card.objectId = objectId
        card.appName = setAppName
        card.appDescription = setAppDescription
        card.appPrompt = setAppPrompt
        card.update(object : UpdateListener() {
            override fun done(ex: BmobException?) {
                if (ex == null) {
                    println("更新成功")
                    Toast.makeText(getApplicationContext(), "卡片更新成功", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(getApplicationContext(), "卡片更新失败", Toast.LENGTH_SHORT)
                        .show()
                    println("更新失败")
                }
            }
        })
    }

    fun upload_from_workshop(
        setAppName: String?,
        setAppDescription: String?,
        setAppPrompt: String?,
        setUserName: String?,
        seticon: String?
    ) {
        var card = AppCenterCard()
        card.appName = setAppName
        card.appDescription = setAppDescription
        card.appPrompt = setAppPrompt
        card.userName = setUserName
        card.icon = seticon

        //保存到数据库
        card.save(object : SaveListener<String?>() {
            override fun done(objectId: String?, e: BmobException?) {
                if (e == null) {
                    mObjectId = objectId
                    Toast.makeText(
                        getApplicationContext(),
                        "应用导入成功",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    println("创建数据失败：" + e.message)
                    Toast.makeText(
                        getApplicationContext(),
                        "应用导入失败",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}
