package com.chatspring

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.chatspring.bmob_data.AppCenterCard

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [deleteApp.newInstance] factory method to
 * create an instance of this fragment.
 */
class deleteApp : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var button_confirm: Button? = null
    private var button_cancel: Button? = null

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
        val view = inflater.inflate(R.layout.fragment_delete_app, container, false)

        button_confirm = view?.findViewById(R.id.button_confirm)
        button_confirm?.setOnClickListener {
            //获取传来的index
            val index = arguments?.getInt("index")

            //提取bmob_mObjectId_list对应的objectId
            val objectId = bmob_mObjectId_list[index!!]

            println(objectId)

            val card= AppCenterCard()
            card.delete_card(objectId)


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

        button_cancel = view?.findViewById(R.id.button_cancel)
        button_cancel?.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            transaction?.replace(R.id.fragment_main, appCenter())?.commit()
        }

        // Inflate the layout for this fragment

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment deleteApp.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            deleteApp().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}