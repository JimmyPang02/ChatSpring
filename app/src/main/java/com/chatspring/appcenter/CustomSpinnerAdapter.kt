package com.chatspring.appcenter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.chatspring.R


class CustomSpinnerAdapter(context: Context, resource: Int, private val data: List<String>) :
    ArrayAdapter<String>(context, resource, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = convertView as TextView? ?: LayoutInflater.from(context).inflate(
            R.layout.custom_spinner_item, parent, false
        ) as TextView
        textView.text = data[position]
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f) // 设置字体大小 好像无效
        return textView
    }
}