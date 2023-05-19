package com.chatspring.appsetting

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.chatspring.R
import com.chatspring.bmob_data.MyUser


class MainAfterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 使用布局文件fragment_main作为该Fragment的界面
        val newView = inflater.inflate(R.layout.fragment_mainafter, container, false)
        // 获取SharedPreferences中存储的账号信息
        //可到时传给别人
        val sharedPreferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        // val button = view.findViewById<Button>(R.id.button)

        //查询Bmob后端云中的数据
        val query = BmobQuery<MyUser>()
        query.addWhereEqualTo("username", username)
        query.findObjects(object : FindListener<MyUser>() {
            override fun done(users: MutableList<MyUser>?, e: BmobException?) {
                if (e == null) {
                    if (users != null && users.isNotEmpty()) {
                        //获取相同username的MyUser对象
                        val user = users[0]
                        //将对应userID列的值赋给idButton的文本
                        val idButton: Button = newView.findViewById(R.id.id_button)
                        idButton.text = user.nickname.toString()
                    }
                } else {
                    //查询失败，处理错误
                    Log.e(TAG, "Bmob query error: ${e.message}")
                }
            }
        })

        // 添加按钮点击事件    //切换账号
        val idButton: Button = newView.findViewById(R.id.id_button)
        idButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {
//                //先添加
//                add(R.id.fragment_main, InformationFragment())
//                replace(R.id.fragment_main, InformationFragment())
//                addToBackStack(null)
//                commit()
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                //设置转场动画
                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                transaction?.replace(R.id.fragment_main, InformationFragment())?.commit()
            }
        }
        //api按钮
        val apiButton: Button = newView.findViewById(R.id.API_key_button)
        apiButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {
//                replace(R.id.fragment_main, ApiIkeyFragment())
//                addToBackStack(null)
//                commit()
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                //设置转场动画
                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                transaction?.replace(R.id.fragment_main, ApiIkeyFragment())?.commit()
            }
        }
        //设置按钮
        val settingButton: Button = newView.findViewById(R.id.setting_button)
        settingButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {

                val transaction = activity?.supportFragmentManager?.beginTransaction()
                //设置转场动画
                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                transaction?.replace(R.id.fragment_main, InformationFragment())?.commit()
            }
        }
        return newView
    }
}

