package com.chatspring

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chatspring.Model.AppModel
import com.chatspring.appsetting.LoginFragment
import com.chatspring.appsetting.LoginState
import com.chatspring.appsetting.MainFragment
import com.chatspring.bmob_data.AppCenterCard
import java.lang.Thread.sleep
import java.util.Timer
import kotlin.concurrent.schedule

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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


/**
 * A simple [Fragment] subclass.
 * Use the [AppCenter.newInstance] factory method to
 * create an instance of this fragment.
 */
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_center, container, false)


        //加入空白页面激活滚动
        val blankScreen = inflater.inflate(R.layout.blank_screen, null)
        root_layout?.addView(blankScreen)




        root_layout = view?.findViewById(R.id.root_layout)


        val refresh = view?.findViewById<ImageButton>(R.id.refresh)

        val rotation = ObjectAnimator.ofFloat(refresh, "rotation", 0f, 360f)
        rotation.duration = 1000
        rotation.repeatCount = ObjectAnimator.INFINITE
        rotation.interpolator = LinearInterpolator()
        refresh?.setOnClickListener {
            root_layout?.removeAllViews()
            val model = AppCenterCard()
            cardViewList.clear()
            cardModelList.clear()
            bmob_mObjectId_list.clear()
            bmob_model_list.clear()
            model.get_all_cards()

            rotation.start()



            Timer().schedule(1000) {
                view.post {
                    loadAppCard()
                    //rotation.cancel()
                }
            }

            Timer().schedule(2000) {
                view.post {
                    rotation.cancel()
                }
            }

        }

        // 判断用户是否登陆
        if (!LoginState.isLoggedIn) {
            // 如果用户没有登陆，跳转到登陆界面,并且弹出消息框
            Toast.makeText(requireContext(), "请先登陆", Toast.LENGTH_SHORT).show()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.fragment_main, MainFragment())?.commit()
        } else {

            // 如果用户已经登陆，获取用户名
            //从本地的sharedPreference中获取用户名，设置名为my_preferences.xml
            // 获取 SharedPreferences 对象
            val sharedPreferences =
                requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

            // 获取用户名字段的值
            val username = sharedPreferences.getString("username", "")
            GlobaluserName = username.toString()

            val sharedPreferences2 =
                requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            // 获取用户名字段的值
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
        }


//        //渲染完成页面后，自动执行下述代码
//        view?.post {
//            var preLength = 0
//            var currentLength: Int
//            Timer().schedule(0, 1000) {
//                // 定时任务中使用 Handler 将操作发布到 UI 线程
//                Handler(Looper.getMainLooper()).post {
//                    //判断是否有新的卡片加入
//                    currentLength = cardViewList.size
//                    if (currentLength > preLength) {
//                        preLength = currentLength
//                        root_layout?.removeAllViews()
//                        loadAppCard()
//                    }
//                }
//            }
//        }

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

    private fun addCard(root_layout: LinearLayout?) {
        val cardView = layoutInflater.inflate(R.layout.card_layout, null)

        val button = cardView.findViewById<Button>(R.id.button_run)

        val spinner = cardView.findViewById<Spinner>(R.id.spinnerApp)

        val appNameLayout = cardView.findViewById<TextView>(R.id.appNameLayout)

        val appDescriptionLayout = cardView.findViewById<TextView>(R.id.appDescriptionLayout)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinnerApp,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
                        //提取cardViewList中的最后一个cardView的位置
                        val index = cardViewList.size - 1
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
                        //提取cardViewList中的最后一个cardView的位置
                        val index = cardViewList.size - 1
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

        //检测传入参数
        val bundle = arguments
        val appName = bundle?.getString("appName")
        val appDescription = bundle?.getString("appDescription")
        val appPrompt = bundle?.getString("appPrompt")

        appNameLayout.text = appName
        appDescriptionLayout.text = appDescription


        val model = AppModel(appName, appDescription, appPrompt)
        cardModelList.add(model)

        //点击运行按钮，跳转到RunAppFragment
        button?.setOnClickListener {
            //把模型数据传递给RunAppFragment
            val bundle = Bundle()
            bundle.putString("appName", model.appName)
            bundle.putString("appDescription", model.appDescription)
            bundle.putString("appPrompt", model.appPrompt)
            val runApp = runApp()
            runApp.arguments = bundle
            //设置转场动画
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.fragment_main, runApp)?.commit()

        }

        cardViewList.add(cardView)


        root_layout?.addView(cardView, 0)

    }

    private fun preloadCard() {
        val cardView = layoutInflater.inflate(R.layout.card_layout, null)

        val button = cardView.findViewById<Button>(R.id.button_run)

        val spinner = cardView.findViewById<Spinner>(R.id.spinnerApp)

        val appNameLayout = cardView.findViewById<TextView>(R.id.appNameLayout)

        val appDescriptionLayout = cardView.findViewById<TextView>(R.id.appDescriptionLayout)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinnerApp,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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


        val appName = "起名大师"
        val appDescription = "起个好名字"
        val appPrompt = "下面我将输入一件物品的描述，你需要根据描述起一个好名字："

        appNameLayout.text = appName
        appDescriptionLayout.text = appDescription

        val AppModel = AppModel(appName, appDescription, appPrompt)
        cardModelList.add(AppModel)


        cardViewList.add(cardView)


        root_layout?.addView(cardView, 0)
    }

    fun view_controller(appName: String?, appDescription: String?, appPrompt: String?) {
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
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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


        cardViewList.add(cardView)

        cardModelList.add(AppModel(appName, appDescription, appPrompt))


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
            //调用view_controller方法
            view_controller(appName, appDescription, appPrompt)
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
                val runApp = runApp()
                runApp.arguments = bundle
                //设置转场动画
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                transaction?.replace(R.id.fragment_main, runApp)?.commit()
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