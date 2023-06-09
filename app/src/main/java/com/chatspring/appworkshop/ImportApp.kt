package com.chatspring.appworkshop

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.chatspring.R
import com.chatspring.appCenter
import com.chatspring.appsetting.LoginFragment
import com.chatspring.appsetting.LoginState
import com.chatspring.bmob_data.AppCenterCard
import com.chatspring.bmob_data.workshop
import com.google.android.material.bottomnavigation.BottomNavigationView

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_APPNAME = "param1"
private const val ARG_APPINTRO = "param2"
private const val ARG_APPPROMPT = "param3"
private const val ARG_ICON = "param4"


class ImportApp : Fragment() {
    // TODO: Rename and change types of parameters
    private var appname: String? = null
    private var appintro: String? = null
    private var appprompt: String? = null
    private var username: String? = null
    private var icon: String? = null
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            appname = it.getString(ARG_APPNAME)
            appintro = it.getString(ARG_APPINTRO)
            appprompt = it.getString(ARG_APPPROMPT)
            icon = it.getString(ARG_ICON)
        }
    }

    //跳转逻辑
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_import_app, container, false)
        // 设置确认按钮的点击事件
        val confirmButton: Button = view.findViewById(R.id.confirm_button)
        confirmButton.setOnClickListener {

        // 导航栏控制
        navController = Navigation.findNavController(view)
        navView = requireActivity().findViewById(R.id.nav_view)

        // 判断用户是否登陆
            if (!LoginState.isLoggedIn) {
                // 如果用户没有登陆，跳转到登陆界面,并且弹出消息框
                Toast.makeText(requireContext(), "请先登陆", Toast.LENGTH_SHORT).show()
                // 跳转到登陆界面
                // 用navController跳转
                navController.navigate(R.id.AppSettingLoginFragment)
                navView.selectedItemId = R.id.settings

                return@setOnClickListener
            } else {
                // 如果用户已经登陆，获取用户名
                val sharedPreferences =
                    requireActivity().getSharedPreferences("my-preferences", Context.MODE_PRIVATE)
                //username = sharedPreferences .getString("username","")
            }

            // 上传数据到appcenter表
            val upload_card = AppCenterCard()
            val sharedPreferences =
                requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

            // 获取用户名字段的值
            val username = sharedPreferences.getString("username", "")
            upload_card.upload_from_workshop(appname!!, appintro!!, appprompt!!, username!!,icon)

            //传输changed回去
            val bundle = Bundle()
            //changed=1
            bundle.putInt("changed", 1)
            val appCenterFragment = appCenter()
            appCenterFragment.arguments = bundle

            //导入应用后，跳转到appcenter fragment
//            val transaction = activity?.supportFragmentManager?.beginTransaction()
//            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
//            transaction?.replace(R.id.fragment_main, appCenterFragment)?.commit()
            navController.navigate(R.id.appCenterFragment)
            navView.selectedItemId = R.id.menu_app_center


        }
        // 点击取消按钮，返回到AppWorkshop fragment
        val cancelButton: Button = view.findViewById(R.id.cancel_button)
        cancelButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)

            transaction?.replace(R.id.fragment_main, AppWorkshop())?.commit()
        }
        return view
    }

    //参数导入

    //动态根据appname显示不同的应用名称的导出框，如：导入"文本润色"应用，导出“文本翻译”应用
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.app_name)
        textView.text = appname
    }

    companion object {
        fun newInstance(param1: String, param2: String, param3: String,param4:String) =
            ImportApp().apply {
                arguments = Bundle().apply {
                    putString(ARG_APPNAME, param1)
                    putString(ARG_APPINTRO, param2)
                    putString(ARG_APPPROMPT, param3)
                    putString(ARG_ICON,param4)
                }
            }
    }
}