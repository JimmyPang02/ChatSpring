package com.chatspring.appsetting

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.chatspring.R
import com.chatspring.bmob_data.MyUser

class InformationFragment : Fragment() {

    lateinit var alertDialog: AlertDialog // 声明为 lateinit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 使用布局文件fragment_main作为该Fragment的界面
        val newView = inflater.inflate(R.layout.fragment_information, container, false)
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
                        //将对应userID列的值赋给idButton的文本
                        val idButton: Button = newView.findViewById(R.id.changeID_button)
                        idButton.text = user.nickname.toString()
                    }
                } else {
                    //查询失败，处理错误
                    Log.e(ContentValues.TAG, "Bmob query error: ${e.message}")
                }
            }
        })

        //改ID按钮
        val changIDButton: Button = newView.findViewById(R.id.changeID_button)
        changIDButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {

                val transaction = activity?.supportFragmentManager?.beginTransaction()
                //设置转场动画
                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                transaction?.replace(R.id.fragment_main, ChangIDFragment())?.commit()

                // 跳转到一个修改id的界面
                //先添加
//                add(R.id.fragment_main, ChangIDFragment())
//                replace(R.id.fragment_main, ChangIDFragment())
//                addToBackStack(null)
//                commit()
            }
        }

        //切换用户按钮
        val changeUserButton: Button = newView.findViewById(R.id.changeUser_button)
        changeUserButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {
                //登录状态改为false
//                LoginState.isLoggedIn = false
                val transaction = activity?.supportFragmentManager?.beginTransaction()

                //设置转场动画
                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)

                transaction?.replace(R.id.fragment_main, LoginFragment())?.commit()
                //                replace(R.id.fragment_main, LoginFragment())
//                addToBackStack(null)
//                commit()
            }
        }

//        //退出登录按钮
//        val exitButton: Button = newView.findViewById(R.id.exit_button)
//        exitButton.setOnClickListener {
//            fragmentManager?.beginTransaction()?.apply {
//                //登录状态改为false
//                LoginState.isLoggedIn = false
//
//                val transaction = fragmentManager?.beginTransaction()
//
//                //设置转场动画
//                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
//
//                transaction?.replace(R.id.fragment_main, MainFragment())?.commit()
////                replace(R.id.fragment_main, MainFragment())
////                // 后续可能要增加销毁所有fragment
////                commit()
//            }
//        }
        //退出登录按钮
        val exitButton: Button = newView.findViewById(R.id.exit_button)
        exitButton.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            // 设置自定义布局
            val customLayout = layoutInflater.inflate(R.layout.promptbox, null)
            alertDialogBuilder.setView(customLayout)
            // 在自定义布局中找到按钮并设置点击事件
            val positiveButton = customLayout.findViewById<Button>(R.id.positive_button)
            positiveButton.setOnClickListener {
                // 点击"确定"按钮的操作
                //登录状态改为false
                LoginState.isLoggedIn = false

                val transaction = fragmentManager?.beginTransaction()

                //设置转场动画
                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)

                transaction?.replace(R.id.fragment_main, MainFragment())?.commit()
                alertDialog.dismiss() // 关闭对话框
            }
            val negativeButton = customLayout.findViewById<Button>(R.id.negative_button)
            negativeButton.setOnClickListener {
                // 点击"取消"按钮的操作
                // ...

                alertDialog.dismiss() // 关闭对话框
            }

            alertDialog = alertDialogBuilder.create() // 在此处初始化
            alertDialog.show()
        }


//            alertDialogBuilder.setPositiveButton("确定") { dialog, which ->
//                fragmentManager?.beginTransaction()?.apply {
//
//                }
//            }
//            alertDialogBuilder.setNegativeButton("取消") { dialog, which ->
//                // Do nothing
//            }
//            alertDialogBuilder.show()
//        }


        val back_button: Button = newView.findViewById(R.id.back_button)
        back_button.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            transaction?.replace(R.id.fragment_main, MainAfterFragment())?.commit()
        }

        val aboutus_button: Button = newView.findViewById(R.id.about_button)
        aboutus_button.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.fragment_main, SettingFragment())?.commit()
        }

        return newView
    }
}