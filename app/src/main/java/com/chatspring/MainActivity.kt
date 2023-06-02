package com.chatspring

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import cn.bmob.v3.Bmob

const val BmobAppKey = "032b1bb187d4fc1e9cad0ba73d98004f"
//const val BmobAppKey"f4451fde9487c9f6c77dc7af136eca23"

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

        // 获取 NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_main) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onStart() {
        super.onStart()

        // 设置导航栏的点击事件
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_app_center -> {
                    // 判断是否已登录
                    val sharedPreferences =
                        this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
                    if (isLoggedIn) {
                        // 切换到 AppCenterFragment
                        val navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                        navController.navigate(R.id.appCenterFragment, null, navOptions)
                    }
                    true
                }

                R.id.menu_app_workshop -> {
                    // 切换到 AppWorkshopFragment
                    val navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                    navController.navigate(R.id.appWorkshopFragment, null, navOptions)
                    true
                }

                R.id.settings -> {
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

                else -> false
            }
        }
    }
}