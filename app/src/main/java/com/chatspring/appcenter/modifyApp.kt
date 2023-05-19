package com.chatspring

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.chatspring.bmob_data.AppCenterCard

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [modifyApp.newInstance] factory method to
 * create an instance of this fragment.
 */
class modifyApp : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var button_cancel: Button? = null
    private var button_finish: Button? = null
    private var setAppName: EditText? = null
    private var textView_Input: EditText? = null
    private var setPrompt: EditText? = null

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
        val view = inflater.inflate(R.layout.fragment_modify_app, container, false)
        //点击取消按钮,跳转到AppCenterFragment
        button_cancel = view?.findViewById(R.id.button_cancel_modify)
        button_cancel?.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            transaction?.replace(R.id.fragment_main, appCenter())?.commit()
        }
        //点击完成按钮，跳转到AppCenterFragment
        button_finish = view?.findViewById(R.id.button_finish)
        setAppName = view?.findViewById(R.id.setAppName)
        textView_Input = view?.findViewById(R.id.textView_Input)
        setPrompt = view?.findViewById(R.id.setPrompt)

        val bundle = arguments
        val index = bundle?.getInt("index")
        val appName = setAppName?.text.toString()
        val appDescription = textView_Input?.text.toString()
        val appPrompt = setPrompt?.text.toString()

        //取出对应model
        val model = cardModelList[index!!]

        //设置appName的内容
        setAppName?.setText(model.appName)
        //设置appDescription的内容
        textView_Input?.setText(model.appDescription)
        //设置appPrompt的内容
        setPrompt?.setText(model.appPrompt)




        button_finish?.setOnClickListener {
            //取出list
            val card = cardViewList[index]
            //修改model
            model.appName = appName
            model.appDescription = appDescription
            model.appPrompt = appPrompt
            //修改list
            val appname = card.findViewById<TextView>(R.id.appNameLayout)
            val appdescription = card.findViewById<TextView>(R.id.appDescriptionLayout)

            //提取bmob_mObjectId_list对应的objectId
            val objectId = bmob_mObjectId_list[index]

            //从输入到的内容中提取出appNameonline,appDescriptiononline,appPromptonline
            val appNameonline = setAppName?.text.toString()
            val appDescriptiononline = textView_Input?.text.toString()
            val appPromptonline = setPrompt?.text.toString()

            println(objectId)

            val card_online = AppCenterCard()
            card_online.upgrade_card(objectId, appNameonline, appDescriptiononline, appPromptonline)



            appname.setText(appName)
            appdescription.setText(appDescription)
            //跳转到AppCenterFragment
            //传输changed回去
            val bundle=Bundle()
            //changed=1
            bundle.putInt("changed",1)
            val appCenterFragment=appCenter()
            appCenterFragment.arguments=bundle

            val transaction = activity?.supportFragmentManager?.beginTransaction()

            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)

            transaction?.replace(R.id.fragment_main, appCenterFragment)?.commit()

        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment modifyApp.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            modifyApp().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}