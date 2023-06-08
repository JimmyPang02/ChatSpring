package com.chatspring

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.aallam.openai.api.BetaOpenAI
import com.chatspring.openAI.chatGPT
import com.chatspring.openAI.chatGPT_flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [runApp.newInstance] factory method to
 * create an instance of this fragment.
 */
class runApp : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var button_execute: Button? = null
    private var button_copy_result: Button? = null
    private var button_return: Button? = null
    private var textView_resultShow: TextView? = null
    private var textView_Input: TextView? = null
    private var textView_appInfo: TextView? = null
    private var textView_description: TextView? = null


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
        val view = inflater.inflate(R.layout.fragment_run_app, container, false)

        val bundle = arguments
        val appName = bundle?.getString("appName")
        val appPrompt = bundle?.getString("appPrompt")
        val appDescription = bundle?.getString("appDescription")
        val icon = bundle?.getString("icon")

        val cardicon = view?.findViewById<ImageView>(R.id.cardiconrun)

        var resourceId = resources.getIdentifier(icon, "drawable", activity?.packageName)
        // 如果找不到则使用默认的"chat"资源ID
        if (resourceId == 0) {
            var temp = resources.getIdentifier("chat", "drawable", activity?.packageName)
            resourceId = temp
        }

        //把icon设置到cardicon
        cardicon?.setImageResource(resourceId)

        textView_appInfo = view?.findViewById(R.id.textView_appInfo)
        if (appName != null) {
            textView_appInfo?.text = appName
        }
        textView_description = view?.findViewById(R.id.textView_description)
        if (appDescription != null) {
            textView_description?.text = appDescription
        }

        //点击返回按钮，跳转到AppCenter
        button_return = view?.findViewById(R.id.button_return)
        button_return?.setOnClickListener {
            //设置转场动画
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            //设置转场动画
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)

            transaction?.replace(R.id.fragment_main, appCenter())?.commit()
        }

        //点击复制结果按钮，复制结果到剪切板
        button_copy_result = view?.findViewById(R.id.button_copy_result)
        textView_resultShow = view?.findViewById(R.id.textView_resultShow)
        button_copy_result?.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("simple text", textView_resultShow?.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()

        }

        //点击执行按钮，执行代码
        button_execute = view?.findViewById(R.id.button_execute)
        textView_Input = view?.findViewById(R.id.textView_Input)
        val scrollView2 = view?.findViewById<ScrollView>(R.id.scrollView2)
        val textView_resultShow = view?.findViewById<TextView>(R.id.textView_resultShow)
        val prompt: String = appPrompt.toString()
        var input = textView_Input?.text.toString()
        var coroutineRunning = false
        val mainScrollView = view?.findViewById<ScrollView>(R.id.mainScrollView)

        //设置textView_resultShow的最低高度
        textView_resultShow?.getViewTreeObserver()?.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    textView_resultShow?.getViewTreeObserver()?.removeOnGlobalLayoutListener(this)
                    val height = textView_resultShow?.getHeight()
                    val minHeight = height?.plus(160)
                    textView_resultShow?.setMinHeight(minHeight!!)
                }
            })


        //渲染完成后执行
        mainScrollView?.post {
            var flag = 0

            //监听textView_resultShow的高度变化，如果高度变化，就滚动到mainScrollView最底部
            mainScrollView.getViewTreeObserver()?.addOnGlobalLayoutListener() {
                if (flag == 1) {
                    mainScrollView.fullScroll(View.FOCUS_DOWN);
                } else {
                    flag = 1
                }
            }
        }

        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val gpt_version = sharedPreferences.getString("gpt_version", "GPT_3.5")

        //把gpt_version显示在textView_resultShow
        textView_resultShow?.text = "使用的GPT版本: $gpt_version"


        button_execute?.setOnClickListener {

            val apiKey = GlobalapiKey
            //检测apiKey是否为空
            if (apiKey == "") {
                Toast.makeText(
                    activity,
                    "未设置API Key，请前往设置界面填入API Key",
                    Toast.LENGTH_SHORT
                ).show()
                //写入textView_resultShow
                textView_resultShow?.text = "未设置API Key，请前往设置界面填入API Key"
                return@setOnClickListener
            }

            if (!coroutineRunning) {
                button_execute?.isEnabled = false
                coroutineRunning = true

                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val shared_Preferences =
                            requireActivity().getSharedPreferences(
                                "MyPrefs",
                                Context.MODE_PRIVATE
                            )
                        val stored_gpt_version =
                            shared_Preferences.getString("gpt_version", "GPT_3.5")

                        coroutineRunning = true
                        button_execute?.isEnabled = false
                        button_execute?.text = "运行中..."
                        textView_resultShow?.text = ""
                        input = textView_Input?.text.toString()

                        //启动滚动条
                        textView_resultShow?.movementMethod =
                            ScrollingMovementMethod.getInstance()


                        stored_gpt_version?.let { it1 ->
                            chatGPT_flow(prompt, input, it1).collect { chunk ->
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
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            textView_resultShow?.text = "出现错误：${e.message}"
                        }
                    } finally {
                        coroutineRunning = false
                        button_execute?.isEnabled = true
                        button_execute?.text = "运行"
                    }
                }


            }
        }

        val button_share = view?.findViewById<Button>(R.id.button_share)
        button_share?.setOnClickListener {
            // 创建shareimg.xml的布局
            val shareView = LayoutInflater.from(context).inflate(R.layout.shareimg, null)
            val shareView_textview_appname = shareView.findViewById<TextView>(R.id.textview_appname)
            val shareView_textview_appdescription =
                shareView.findViewById<TextView>(R.id.textview_appdescription)
            val shareView_textview_myinput = shareView.findViewById<TextView>(R.id.textview_myinput)
            val shareView_textview_response =
                shareView.findViewById<TextView>(R.id.textview_response)

            val cardiconshare = shareView.findViewById<ImageView>(R.id.cardiconshare)

            // 设置shareView的内容
            shareView_textview_appname.text = appName
            shareView_textview_appdescription.text = appDescription
            shareView_textview_myinput.text = textView_Input?.text.toString()
            shareView_textview_response.text = textView_resultShow?.text.toString()

            //把resourceid内容设置到shareView
            //把icon设置到cardicon
            cardiconshare?.setImageResource(resourceId)


            // 将shareView转换为bitmap
            val displayMetrics = context?.resources?.displayMetrics
            val width = displayMetrics?.widthPixels ?: 0

            // 先测量 shareView
            shareView.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            // 获取shareView的实际高度
            val actualHeight = shareView.measuredHeight

            // 使用实际高度进行布局
            shareView.layout(0, 0, width, actualHeight)

            // 使用实际高度创建位图
            val bitmap = Bitmap.createBitmap(width, actualHeight, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bitmap)
            shareView.draw(canvas)


            // 把bitmap转换为png并分享到外部
            val file =
                File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "shareimg.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val fileUri = FileProvider.getUriForFile(
                context!!,
                "${BuildConfig.APPLICATION_ID}.provider",
                file
            )
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share image via:"))
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
         * @return A new instance of fragment runApp.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            runApp().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}