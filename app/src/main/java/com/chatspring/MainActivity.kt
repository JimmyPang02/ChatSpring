package com.chatspring

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import cn.bmob.v3.Bmob

const val BmobAppKey = "032b1bb187d4fc1e9cad0ba73d98004f"
//const val BmobAppKey = "f4451fde9487c9f6c77dc7af136eca23"

class MainActivity : AppCompatActivity() {

    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //更改顶部颜色
        val window: Window = window
        window.statusBarColor = resources.getColor(R.color.white_trans)//白色半透明
        //更改顶部字体颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            decorView.systemUiVisibility =
                decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        Bmob.initialize(this, BmobAppKey)

        //隐藏顶部标题栏
        supportActionBar?.hide()

        // 导航栏
        navView = findViewById(R.id.nav_view)

        // 获取 NavController(可以理解为导航栏的body)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_main) as NavHostFragment
        navController = navHostFragment.navController

        // 根据用户登录状态初始界面
        val sharedPreferences =
            this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            // 如果未登录，导航至 AppSetting
            navController.navigate(R.id.AppSetting_notlogin)
            navView.selectedItemId = R.id.settings
        } else {
            // 如果已登录，导航至 appCenterFragment
            navController.navigate(R.id.appCenterFragment)
            navView.selectedItemId = R.id.menu_app_center
        }


        // 设置导航栏的点击事件
        navView.setOnNavigationItemSelectedListener { item ->

            // 点击事件
            when (item.itemId) {
                R.id.menu_app_center -> {
                    // 获取登录状态
                    val sharedPreferences =
                        this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

                    // 判断是否登录
                    if (!isLoggedIn) {
                        // toast提示
                        Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
                        // 切换到 AppSetting （未登录）
                        val navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                        navController.navigate(R.id.AppSetting_notlogin, null, navOptions)
                        false // 返回 false 用于取消选择事件，使得导航栏的选中值不会被切换
                    } else {
                        // 切换到 AppCenterFragment
                        val navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                        navController.navigate(R.id.appCenterFragment, null, navOptions)
                        true
                    }
                }

                R.id.menu_app_workshop -> {
                    // 获取登录状态
                    val sharedPreferences =
                        this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
                    if (!isLoggedIn) {
                        // toast提示
                        Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
                        // 切换到 AppSetting （未登录）
                        val navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                        navController.navigate(R.id.AppSetting_notlogin, null, navOptions)
                        false // 返回 false 用于取消选择事件，使得导航栏的选中值不会被切换
                    }
                    else {
                        // 切换到 AppWorkshopFragment
                        val navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                        navController.navigate(R.id.appWorkshopFragment, null, navOptions)
                        true
                    }
                }

                R.id.settings -> {
                    // 获取登录状态
                    val sharedPreferences =
                        this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

                    if (!isLoggedIn) {
                        // 切换到 AppSetting （未登录）
                        val navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                        navController.navigate(R.id.AppSetting_notlogin, null, navOptions)
                    } else if (isLoggedIn) {
                        // 切换到 AppSetting (已登录)
                        val navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                        navController.navigate(R.id.AppSetting_islogin, null, navOptions)
                    }
                    true
                }


                // 返回 true 表示已处理选择事件
                else -> false
            }
        }

    }
}