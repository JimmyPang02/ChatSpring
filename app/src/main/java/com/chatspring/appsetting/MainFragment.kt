package com.chatspring.appsetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.chatspring.R

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // 使用布局文件fragment_main作为该Fragment的界面
        val newView = inflater.inflate(R.layout.activity_main_fragment, container, false)

        //登录按钮
        // val button = view.findViewById<Button>(R.id.button)
        // 添加按钮点击事件
        val loginButton: Button = newView.findViewById(R.id.login_register_button)
        loginButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {

                val transaction = activity?.supportFragmentManager?.beginTransaction()

                //设置转场动画
                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)

                transaction?.replace(R.id.fragment_main, LoginFragment())?.commit()
            }
        }

        //api按钮
        val apiButton: Button = newView.findViewById(R.id.API_key_button)
        apiButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {
                val transaction = activity?.supportFragmentManager?.beginTransaction()

                //设置转场动画
                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)

                transaction?.replace(R.id.fragment_main, ApiIkeyFragment())?.commit()
//                //先添加
//                add(R.id.fragment_main,ApiIkeyFragment())
//                replace(R.id.fragment_main, ApiIkeyFragment())
//                addToBackStack(null)
//                commit()
            }
        }

        //设置按钮
        val settingButton: Button = newView.findViewById(R.id.setting_button)
        settingButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {

                val transaction = activity?.supportFragmentManager?.beginTransaction()

                //设置转场动画
                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)

                transaction?.replace(R.id.fragment_main, LoginFragment())?.commit()
                //                add(R.id.fragment_main,SettingFragment())
//                replace(R.id.fragment_main, LoginFragment())
//                addToBackStack(null)
//                commit()
            }
//            // 找到 FragmentManager 并开启事务
//            val fragmentManager = requireActivity().supportFragmentManager
//            val transaction = fragmentManager.beginTransaction()
//
//            // 替换当前显示的 Fragment 为 MainFragment
//            transaction.replace(R.id.fragment_main, LoginFragment())
////            transaction.addToBackStack(null)
//
//            // 提交事务
//            transaction.commit()


//            fragmentManager?.beginTransaction()?.apply {
//                replace(R.id.fragment_main, SettingFragment())
//                addToBackStack(null)
//                commit()
        }




        return newView
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        // val inflater = LayoutInflater.from(requireContext())
//
//        val newView = inflater.inflate(R.layout.activity_main_fragment, container, false)
//
//
//        // val button = view.findViewById<Button>(R.id.button)
//        // 添加按钮点击事件
//        val loginButton: Button = newView.findViewById(R.id.login_register_button)
//        loginButton.setOnClickListener {
//            fragmentManager?.beginTransaction()?.apply {
//                replace(R.id.fragment_main, LoginFragment())
//                addToBackStack(null)
//                commit()
//            }
//        }
//    }

}