package com.chatspring.appsetting

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import com.chatspring.R
import com.chatspring.bmob_data.MyUser

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        // 获取注册按钮并添加点击事件
        val registerButton: Button = view.findViewById(R.id.register_button)
        registerButton.setOnClickListener {
            // 创建RegisterFragment实例并将其添加到fragment_main中
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.fragment_main, RegisterFragment())?.commit()
        }
        val loginButton: Button = view.findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            val username = view.findViewById<EditText>(R.id.username_edit_text).text.toString()
            val password = view.findViewById<EditText>(R.id.password_edit_text).text.toString()
            val userlogin = BmobUser()
            userlogin.username = view.findViewById<EditText>(R.id.username_edit_text).text.toString()
            userlogin.setPassword(view.findViewById<EditText>(R.id.password_edit_text).text.toString())
            userlogin.login(object : SaveListener<BmobUser>() {
                override fun done(bmobUser: BmobUser?, e: BmobException?) {
                    if (e == null) {
                        Toast.makeText(requireContext(), bmobUser?.username + "登录成功", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("登录失败", "原因: ", e)
                    }
                }
            })

            // 创建BmobQuery实例
            val query = BmobQuery<MyUser>()
            // 设置查询条件
            query.addWhereEqualTo("username", username)
            query.addWhereEqualTo("password", password)
            // 执行查询操作
            query.findObjects(object : FindListener<MyUser>() {
                override fun done(users: MutableList<MyUser>?, e: BmobException?) {
                    if (e == null) {
                        if (users != null && users.isNotEmpty()) {
                            //登录状态为true
                            LoginState.isLoggedIn = true

                            // 在登录成功后，将账号信息存储在 SharedPreferences 中

                            val editor = sharedPreferences.edit()
                            editor.putString("username", username)
                            editor.putString("password", password)
                            editor.putBoolean("isLoggedIn", true)
                            editor.apply()

                            // 登录成功，跳转到成功主界面
                            fragmentManager?.beginTransaction()?.apply {
                                add(R.id.fragment_main, MainAfterFragment())
                                replace(R.id.fragment_main, MainAfterFragment())
                                addToBackStack(null)
                                commit()
                            }
                        } else {
                            // 登录失败，弹出提示框
                            AlertDialog.Builder(requireContext())
                                .setTitle("登录失败")
                                .setMessage("用户名或密码错误，请重试！")
                                .setPositiveButton("确定") { dialog, which ->
                                    // 用户点击确定按钮后，对话框消失
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    } else {
                        // 查询失败，弹出提示框
                        AlertDialog.Builder(requireContext())
                            .setTitle("登录失败")
                            .setMessage("查询异常，请重试！")
                            .setPositiveButton("确定") { dialog, which ->
                                // 用户点击确定按钮后，对话框消失
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
            })
        }
        val back_button: Button = view.findViewById(R.id.back_button)
        back_button.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            val fragment = if (LoginState.isLoggedIn) InformationFragment() else MainFragment()
            transaction?.replace(R.id.fragment_main, fragment)?.commit()
        }
        return view
    }
}