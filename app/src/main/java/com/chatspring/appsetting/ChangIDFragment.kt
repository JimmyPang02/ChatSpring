package com.chatspring.appsetting

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.chatspring.R
import com.chatspring.bmob_data.MyUser

class ChangIDFragment : Fragment() {

    override fun onCreateView(inflater:
                              LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_changid, container, false)

        // 获取SharedPreferences中存储的账号信息
        val sharedPreferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")

        //查询Bmob后端云中的数据
        val query = BmobQuery<MyUser>()
        query.addWhereEqualTo("username", username)
        query.findObjects(object : FindListener<MyUser>() {
            override fun done(users: MutableList<MyUser>?, e: BmobException?) {
                if (e == null) {
                    if (users != null && users.isNotEmpty()) {
                        //获取相同username的MyUser对象
                        val user = users[0]
                        //将对应userID列的值赋给editTextTextPersonName的文本
                        val editText: EditText = view.findViewById(R.id.editTextTextPersonName)
                        editText.setText(user.nickname)
                    }
                } else {
                    //查询失败，处理错误
                    Log.e(TAG, "Bmob query error: ${e.message}")
                }
            }
        })

        // 获取保存按钮并添加点击事件
        val saveIDButton: Button = view.findViewById<Button>(R.id.saveID_button)
        saveIDButton.setOnClickListener {
            val newNickname: String = view.findViewById<EditText>(R.id.editTextTextPersonName).text.toString().trim()
            // 获取SharedPreferences中存储的账号信息
            val sharedPreferences1 = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val username1 = sharedPreferences1.getString("username", "")
            //查询Bmob后端云中的数据
            val query1 = BmobQuery<MyUser>()
            query1.addWhereEqualTo("username", username1)
            query1.findObjects(object : FindListener<MyUser>() {
                override fun done(users1: MutableList<MyUser>?, e: BmobException?) {
                    if (e == null) {
                        if (users1 != null && users1.isNotEmpty()) {
                            //获取相同username的MyUser对象
                            val user2 = users1[0]
                            user2.nickname = newNickname
                            user2.update(user2.objectId, object : UpdateListener() {
                                override fun done(e: BmobException?) {
                                    if (e == null) {
                                        // 更新成功，返回上一个Fragment
                                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                                        transaction?.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_in_right)
                                        transaction?.replace(R.id.fragment_main, InformationFragment())?.commit()
                                    } else {
                                        // 更新失败，处理错误
                                        Log.e(TAG, "Bmob update error: ${e.message}")
                                    }
                                }
                            })
                        }
                    } else {
                        //查询失败，处理错误
                        Log.e(TAG, "Bmob query error: ${e.message}")
                    }
                }
            })
        }

        val back_button: Button = view.findViewById(R.id.back_button)
        back_button.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            transaction?.replace(R.id.fragment_main, InformationFragment())?.commit()
        }

        return view
    }
}