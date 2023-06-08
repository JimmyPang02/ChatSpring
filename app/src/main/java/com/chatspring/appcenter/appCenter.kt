package com.chatspring

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.updateMargins
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.chatspring.Model.AppModel
import com.chatspring.appsetting.LoginState
import com.chatspring.appsetting.MainFragment
import com.chatspring.bmob_data.AppCenterCard
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.sql.Time
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule


// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
val cardViewList = mutableListOf<View>()
val cardModelList = mutableListOf<AppModel>()

//string数组，存放mObjectId
val bmob_mObjectId_list = mutableListOf<String>()
val bmob_model_list = mutableListOf<AppCenterCard>()

var GlobaluserName = ""
var GlobalapiKey = ""

var initial: Int = 1


class appCenter : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var button_createApp: Button? = null
    private var root_layout: LinearLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

//    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val isGetAllCards = intent.getBooleanExtra("isGetAllCards", false)
//            if (isGetAllCards) {
//                //等待0.5秒
//                val timer = Timer()
//                timer.schedule(object : TimerTask() {
//                    override fun run() {
//                        // 需要一个Activity的实例，如果在Fragment中可以用getActivity()
//                        val activity: Activity? = activity
//                        activity?.runOnUiThread {
//                            // 更新UI
//                            root_layout?.removeAllViews()
//                            // 启动协程
//                            loadAppCard()
//                        }
//                    }
//                }, 100)
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        val intentFilter = IntentFilter("com.chatspring.appCenter")
//        LocalBroadcastManager.getInstance(requireActivity())
//            .registerReceiver(receiver, intentFilter)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver)
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        //初始化卡片使用的Bmob
        Bmob.initialize(requireContext(), BmobAppKey)

        val view = inflater.inflate(R.layout.fragment_app_center, container, false)


        root_layout = view?.findViewById(R.id.root_layout)


        val refresh = view?.findViewById<ImageButton>(R.id.refresh)

        val rotation = ObjectAnimator.ofFloat(refresh, "rotation", 0f, 360f)
        rotation.duration = 2000
        rotation.repeatCount = ObjectAnimator.INFINITE
        rotation.interpolator = LinearInterpolator()
        refresh?.setOnClickListener {
            root_layout?.removeAllViews()
            //加入空白页面激活滚动
            val blankScreen = inflater.inflate(R.layout.blank_screen, null)
            //往末尾加入
            //root_layout?.addView(blankScreen)
            val model = AppCenterCard()
            cardViewList.clear()
            cardModelList.clear()
            bmob_mObjectId_list.clear()
            bmob_model_list.clear()
            model.get_all_cards()

            rotation.start()

        }

        val context = requireContext()

        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val isGetAllCards = intent.getBooleanExtra("isGetAllCards", false)
                if (isGetAllCards) {
                    val timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            // 需要一个Activity的实例，如果在Fragment中可以用getActivity()
                            val activity: Activity? = activity
                            activity?.runOnUiThread {
                                // 更新UI
                                root_layout?.removeAllViews()
                                // 更新卡片
                                loadAppCard()
                                rotation.cancel()
                            }
                        }
                    }, 200)
                }
            }
        }

        // 注册广播接收器
        val intentFilter = IntentFilter("com.chatspring.appCenter")
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter)


        val sharedPreferences =
            requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // 判断用户是否登陆 （这个地方理论上已经没用了，进入appcenter的时候会保证一定是登陆态）
        if (!isLoggedIn) {
            // 设置底部导航栏的选中项为设置
            val bottomNavigationView =
                activity?.findViewById<BottomNavigationView>(R.id.nav_view)
            bottomNavigationView?.selectedItemId = R.id.settings
            // 如果用户没有登陆，跳转到登陆界面,并且弹出消息框
            Toast.makeText(requireContext(), "请先登陆", Toast.LENGTH_SHORT).show()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.fragment_main, MainFragment())?.commit()

        } else {

            LoginState.isLoggedIn = true
            // 获取用户名字段的值
            val username = sharedPreferences.getString("username", "")
            val password = sharedPreferences.getString("password", "")
            GlobaluserName = username.toString()
            val userlogin = BmobUser()
            userlogin.username = username.toString()
            userlogin.setPassword(password.toString())
            userlogin.login(object : SaveListener<BmobUser>() {
                override fun done(bmobUser: BmobUser?, e: BmobException?) {
                    if (e == null) {
                        //Toast.makeText(requireContext(), bmobUser?.username + "登录成功", Toast.LENGTH_SHORT).show()
                    } else {
                        //Log.e("登录失败", "原因: ", e)
                    }
                }
            })

            val sharedPreferences2 =
                requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            // 获取apikey字段的值
            val apiKey = sharedPreferences2.getString("apiKey", "")
            GlobalapiKey = apiKey.toString()

            view?.post {

                if (initial == 1) {
                    initial = 0
                    refresh?.performClick()
                }
            }
        }


        view?.post {
            root_layout?.removeAllViews()
            loadAppCard()
            //加入空白页面激活滚动
            val blankScreen = inflater.inflate(R.layout.blank_screen, null)
            //往末尾加入
            //root_layout?.addView(blankScreen)
        }


        //接受传入的changed
        val bundle = arguments
        if (bundle != null) {
            val changed = bundle.getInt("changed")
            if (changed == 1) {
                refresh?.performClick()
            }
        }


        //点击创建应用按钮,跳转到CreateAppFragment
        button_createApp = view?.findViewById(R.id.button_createApp)
        button_createApp?.setOnClickListener {
            val fragment = createApp()
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)

            transaction?.replace(R.id.fragment_main, fragment)?.commit()
        }

        //loadAppCard()


        // Inflate the layout for this fragment
        return view
    }

    fun view_controller(
        appName: String?,
        appDescription: String?,
        appPrompt: String?,
        icon: String?
    ) {
        val cardView = LayoutInflater.from(activity).inflate(R.layout.card_layout, null)

        val button = cardView.findViewById<Button>(R.id.button_run)

        val spinner = cardView.findViewById<Spinner>(R.id.spinnerApp)

        val appNameLayout = cardView.findViewById<TextView>(R.id.appNameLayout)

        val appDescriptionLayout = cardView.findViewById<TextView>(R.id.appDescriptionLayout)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinnerApp,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_item)
        spinner?.adapter = spinnerAdapter

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    1 -> {  // 修改按钮选择时
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fragment_main, modifyApp())?.commit()
                    }

                    2 -> {  // 删除按钮选择时
                        //提取cardViewList中的最后一个cardView的位置
                        val index = cardViewList.size - 1
                        //传递index给deleteApp
                        val bundle = Bundle()
                        bundle.putInt("index", index)
                        val deleteApp = deleteApp()
                        deleteApp.arguments = bundle
                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                        transaction?.setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        transaction?.replace(R.id.fragment_main, deleteApp)?.commit()

                    }

                    3 -> {  // 分享按钮选择时
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fragment_main, shareApp())?.commit()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 未选择任何选项时的逻辑
            }
        }

        //点击运行按钮，跳转到RunAppFragment
        button?.setOnClickListener {

            //设置转场动画
            val transaction = activity?.supportFragmentManager?.beginTransaction()


            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.fragment_main, runApp())?.commit()
        }


        appNameLayout.text = appName
        appDescriptionLayout.text = appDescription

        val cardicon = cardView.findViewById<ImageView>(R.id.cardicon)

        var resourceId = resources.getIdentifier(icon, "drawable", activity?.packageName)
        // 如果找不到则使用默认的"chat"资源ID
        if (resourceId == 0) {
            var temp = resources.getIdentifier("chat", "drawable", activity?.packageName)
            resourceId = temp
        }

        //把icon设置到cardicon
        cardicon.setImageResource(resourceId)





        cardViewList.add(cardView)

        cardModelList.add(AppModel(appName, appDescription, appPrompt, icon))


        root_layout?.addView(cardView, 0)
    }

    fun bmob_to_model() {
        //遍历bmob_model_list
        for (i in 0 until bmob_model_list.size) {
            //获取bmob_model_list中的每一个bmob_model
            val bmob_model = bmob_model_list[i]
            //获取bmob_model中的appName和appDescription
            val appName = bmob_model.appName
            val appDescription = bmob_model.appDescription
            val appPrompt = bmob_model.appPrompt
            val icon = bmob_model.icon
            //调用view_controller方法
            view_controller(appName, appDescription, appPrompt, icon)
        }
    }

    fun loadAppCard() {


        //清空所有cardViewList
        cardViewList.clear()

        //清空所有cardModelList
        cardModelList.clear()



        bmob_to_model()


        // 读取列表中的所有卡片并添加到根视图
        cardViewList.forEach {
            //取得当前序号
            val index = cardViewList.indexOf(it)
            //清空所有cardViewList的所有parent
            val parent = it.parent as ViewGroup
            parent.removeView(it)

            // 设置卡片的下边距
            val marginBottomInDp = 8
            val marginBottomInPx = (marginBottomInDp * it.resources.displayMetrics.density).toInt()
            val layoutParams = it.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.updateMargins(bottom = marginBottomInPx)
            it.layoutParams = layoutParams

            // 将卡片添加到根视图
            root_layout?.addView(it, 0)

            val cardButton = it.findViewById<Button>(R.id.button_run)
            //设置监听
            cardButton.setOnClickListener {
                //提取对应的模型
                val model = cardModelList[index]
                val bundle = Bundle()
                bundle.putString("appName", model.appName)
                bundle.putString("appDescription", model.appDescription)
                bundle.putString("appPrompt", model.appPrompt)
                bundle.putString("icon", model.icon)
                val runApp = runApp()
                runApp.arguments = bundle
                //设置转场动画
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                transaction?.replace(R.id.fragment_main, runApp)?.commit()
            }


            val appNameLayout = it.findViewById<TextView>(R.id.appNameLayout)
            val appDescriptionLayout = it.findViewById<TextView>(R.id.appDescriptionLayout)
            //设置appNameLayout的监听
            appNameLayout.setOnClickListener {
                // 修改按钮选择时
                //传递index给modifyApp
                val bundle = Bundle()
                bundle.putInt("index", index)
                val modifyApp = modifyApp()
                modifyApp.arguments = bundle
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                transaction?.replace(R.id.fragment_main, modifyApp)?.commit()
            }

            //设置appDescriptionLayout的监听
            appDescriptionLayout.setOnClickListener {
                // 修改按钮选择时
                //传递index给modifyApp
                val bundle = Bundle()
                bundle.putInt("index", index)
                val modifyApp = modifyApp()
                modifyApp.arguments = bundle
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                transaction?.replace(R.id.fragment_main, modifyApp)?.commit()
            }


            //设置卡片的下拉菜单
            val cardSpinner = it.findViewById<Spinner>(R.id.spinnerApp)


            //重置下拉菜单
            cardSpinner.setSelection(0)
            cardSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        1 -> {  // 修改按钮选择时
                            //传递index给modifyApp
                            val bundle = Bundle()
                            bundle.putInt("index", index)
                            val modifyApp = modifyApp()
                            modifyApp.arguments = bundle
                            val transaction = activity?.supportFragmentManager?.beginTransaction()
                            transaction?.setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            transaction?.replace(R.id.fragment_main, modifyApp)?.commit()
                        }

                        2 -> {  // 删除按钮选择时
                            //传递index给deleteApp
                            val bundle = Bundle()
                            bundle.putInt("index", index)
                            val deleteApp = deleteApp()
                            deleteApp.arguments = bundle
                            val transaction = activity?.supportFragmentManager?.beginTransaction()
                            transaction?.setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            transaction?.replace(R.id.fragment_main, deleteApp)?.commit()

                        }

                        3 -> {  // 分享按钮选择时
                            //传递index给shareApp
                            val bundle = Bundle()
                            bundle.putInt("index", index)
                            val shareApp = shareApp()
                            shareApp.arguments = bundle
                            val transaction = activity?.supportFragmentManager?.beginTransaction()
                            transaction?.setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            transaction?.replace(R.id.fragment_main, shareApp)?.commit()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // 未选择任何选项时的逻辑
                }
            }

        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AppCenter.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            appCenter().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}