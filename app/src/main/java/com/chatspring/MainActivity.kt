package com.chatspring

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import cn.bmob.v3.Bmob
import com.chatspring.appsetting.LoginState

class MainActivity : AppCompatActivity() {

    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化BMOB
//        Bmob.initialize(this,"f4451fde9487c9f6c77dc7af136eca23")
        Bmob.initialize(this,"032b1bb187d4fc1e9cad0ba73d98004f") //注释掉，别删掉
1
        //隐藏顶部标题栏
        supportActionBar?.hide()

        // 导航栏
        navView = findViewById(R.id.nav_view)

        // 获取 NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_main) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onStart() {
        super.onStart()
        // 设置导航栏的点击事件
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_app_center -> {
                    // 切换到 AppCenterFragment
                    val navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                    navController.navigate(R.id.appCenterFragment, null, navOptions)
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
                    if (!LoginState.isLoggedIn) {
                        // 切换到 AppSetting （未登录）
                        val navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                        navController.navigate(R.id.AppSetting_notlogin, null, navOptions)
                    }
                    else if(LoginState.isLoggedIn){
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