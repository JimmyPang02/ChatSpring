package com.chatspring.appsetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.chatspring.R
import com.chatspring.bmob_data.AppCenterCard
import com.chatspring.bmob_data.MyUser

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        // 获取注册按钮并添加点击事件
        val registerButton: Button = view.findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener {
            // 创建MyUser实例并设置相关属性
            val myUser = MyUser()
            myUser.nickname =
                view.findViewById<EditText>(R.id.nickname_edit_text).text.toString() // 设置昵称为UserID
            myUser.username =
                view.findViewById<EditText>(R.id.username_edit_text).text.toString() // 设置用户名
            myUser.setPassword(view.findViewById<EditText>(R.id.password_edit_text).text.toString()) // 设置密码

            // 调用signUp方法进行注册
            myUser.signUp(object : SaveListener<MyUser>() {
                override fun done(myUser: MyUser?, e: BmobException?) {
                    if (e == null) {
                        val username = myUser?.username.toString()
                        //上传到Bmob云数据库
                        val model = AppCenterCard()

                        model.create_card(
                            "Slogan一键生成",
                            "输入你的产品名称和最主要功能，将为你创建一句更吸引人的广告语Slogan",
                            "我想让你帮我撰写一句经典、有冲击力的应用开屏广告语，接下来我将提供我的产品名和核心功能，如下：",
                            username,
                        )
                        model.create_card(
                            "句子转表情",
                            "用表情包把语句的内容展示出来",
                            "我要你把我写的句子翻译成表情符号。我会写句子，你会用表情符号表达它。我只是想让你用表情符号来表达它。除了表情符号，我不希望你回复任何内容。当我需要用英语告诉你一些事情时，我会用 {like this} 这样的大括号括起来。我的第一句话是：",
                            username,
                        )
                        model.create_card(
                            "想聊就聊",
                            "想聊啥就聊啥",
                            "",
                            username,
                        )


                        // 注册成功，返回到登录页面
                        Toast.makeText(requireContext(), "注册成功", Toast.LENGTH_SHORT).show()
                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                        //设置转场动画
                        transaction?.setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        transaction?.replace(R.id.fragment_main, LoginFragment())?.commit()
                    } else {
                        // 注册失败，弹出提示框
                        Toast.makeText(
                            requireContext(),
                            "账号已被注册，请重新输入账号！",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
        val back_button: Button = view.findViewById(R.id.back_button)
        back_button.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            transaction?.replace(R.id.fragment_main, LoginFragment())?.commit()
        }


        return view
    }
}
