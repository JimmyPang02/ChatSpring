package com.chatspring.appsetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.chatspring.R

class SettingFragment : Fragment() {

    override fun onCreateView(inflater:
                              LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        // 设置关于界面的版本号
        val textView = view.findViewById<TextView>(R.id.textView_about)
        val appVersion = getString(R.string.app_version)
        textView.setText(String.format(getString(R.string.current_version), appVersion))

        // 获取返回按钮并添加点击事件
        val back_button: Button = view.findViewById(R.id.back_button)
        back_button.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            transaction?.replace(R.id.fragment_main, InformationFragment())?.commit()
        }

        return view
    }
}
