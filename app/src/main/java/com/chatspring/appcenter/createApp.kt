package com.chatspring

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.aallam.openai.api.BetaOpenAI
import com.chatspring.bmob_data.AppCenterCard
import com.chatspring.openAI.chatGPT_flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateApp.newInstance] factory method to
 * create an instance of this fragment.
 */
class createApp : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var button_cancel: Button? = null
    private var button_finish: Button? = null
    private var setAppName: TextView? = null
    private var textView_Input: TextView? = null
    private var setPrompt: TextView? = null
    private var button_test: Button? = null
    private var textView_resultShow: TextView? = null
    private var setTestData: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @OptIn(BetaOpenAI::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_app, container, false)
        //点击取消按钮，跳转到AppCenterFragment
        button_cancel = view?.findViewById(R.id.button_cancel_modify)
        button_cancel?.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)

            transaction?.replace(R.id.fragment_main, appCenter())?.commit()
        }
        //点击完成按钮，跳转到AppCenterFragment
        button_finish = view?.findViewById(R.id.button_finish)
        setAppName = view?.findViewById(R.id.setAppName)
        textView_Input = view?.findViewById(R.id.textView_Input)
        setPrompt = view?.findViewById(R.id.setPrompt)
        button_finish?.setOnClickListener {


            //上传到Bmob云数据库
            val model = AppCenterCard()
            model.create_card(
                setAppName?.text.toString(),
                textView_Input?.text.toString(),
                setPrompt?.text.toString(),
                GlobaluserName,
            )


            //传输changed回去
            val bundle = Bundle()
            //changed=1
            bundle.putInt("changed", 1)
            val appCenterFragment = appCenter()
            appCenterFragment.arguments = bundle

            val transaction = activity?.supportFragmentManager?.beginTransaction()

            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)

            transaction?.replace(R.id.fragment_main, appCenterFragment)?.commit()


        }

        //点击测试按钮，测试输入的内容
        button_test = view?.findViewById(R.id.button_test)
        textView_resultShow = view?.findViewById(R.id.textView_result)
        setTestData = view?.findViewById(R.id.setTestData)
        val setPrompt = view?.findViewById<TextView>(R.id.setPrompt)
        val scrollView2 = view?.findViewById<ScrollView>(R.id.scrollView)
        //设置textView_resultShow的最低高度
        textView_resultShow?.getViewTreeObserver()?.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    textView_resultShow?.getViewTreeObserver()?.removeOnGlobalLayoutListener(this)
                    val height = textView_resultShow?.getHeight()
                    val minHeight = height?.plus(50)
                    textView_resultShow?.setMinHeight(minHeight!!)
                }
            })


        //监听textView_resultShow的高度变化，如果高度变化，就滚动到最底部
        scrollView2?.getViewTreeObserver()?.addOnGlobalLayoutListener() {
            scrollView2.fullScroll(View.FOCUS_DOWN);
        }
        var coroutineRunning = false
        button_test?.setOnClickListener {

            if (!coroutineRunning) {
                button_test?.isEnabled = false
                coroutineRunning = true

                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val prompt = setPrompt?.text.toString()
                        val input = setTestData?.text.toString()
                        coroutineRunning = true
                        button_test?.isEnabled = false
                        button_test?.text = "测试中..."
                        textView_resultShow?.text = ""

                        //启动滚动条
                        textView_resultShow?.movementMethod =
                            ScrollingMovementMethod.getInstance()

                        chatGPT_flow(prompt, input).collect { chunk ->
                            for (choice in chunk.choices) {
                                val delta = choice.delta
                                delta?.let {
                                    val generatedText = it.content
                                    if (!generatedText.isNullOrEmpty()) {
                                        withContext(Dispatchers.Main) {

                                            textView_resultShow?.append(generatedText + "")
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            textView_resultShow?.text = "出现错误：${e.message}"
                        }
                    } finally {
                        coroutineRunning = false
                        button_test?.isEnabled = true
                        button_test?.text = "测试"
                    }
                }


            }

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
         * @return A new instance of fragment CreateApp.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            createApp().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}