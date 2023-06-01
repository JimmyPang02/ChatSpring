package com.chatspring.appsetting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.chatspring.R

class ApiIkeyFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    //private lateinit var apiurlEditText: EditText
    private lateinit var apikeyEditText: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_apikey, container, false)

        // 初始化 SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // 找到 EditText 视图
        //apiurlEditText = view.findViewById(R.id.apiurl_edit_text)
        apikeyEditText = view.findViewById(R.id.apikey_edit_text)

        // 从 SharedPreferences 中获取已存储的值
        //val storedApiUrl = sharedPreferences.getString("apiUrl", "")
        val storedApiKey = sharedPreferences.getString("apiKey", "")

        // 如果已存储的值不为空，则设置 EditText 的文本
//        if (storedApiUrl!=null) {
//            apiurlEditText.setText(storedApiUrl)
//        }
        if (storedApiKey!=null) {
            apikeyEditText.setText(storedApiKey)
        }

        // 获取保存按钮并添加点击事件
        val saveButton: Button = view.findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            // 获取输入框的值
            //val apiUrl = apiurlEditText.text.toString()
            val apiKey = apikeyEditText.text.toString()

            // 使用 SharedPreferences 存储输入框的值
            val editor = sharedPreferences.edit()
            //editor.putString("apiUrl", apiUrl)
            editor.putString("apiKey", apiKey)
            editor.apply()

            // 根据登录状态导航到 MainAfterFragment 或 MainFragment
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            val fragment = if (LoginState.isLoggedIn) MainAfterFragment() else MainFragment()
            transaction?.replace(R.id.fragment_main, fragment)?.commit()
        }

        val back_button: Button = view.findViewById(R.id.back_button)
        back_button.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            val fragment = if (LoginState.isLoggedIn) MainAfterFragment() else MainFragment()
            transaction?.replace(R.id.fragment_main, fragment)?.commit()
        }

        return view
    }
}
