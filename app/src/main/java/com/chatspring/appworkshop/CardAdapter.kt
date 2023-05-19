package com.chatspring.appworkshop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chatspring.R

/*
这段代码定义了一个RecyclerView的Adapter用于显示卡片列表。它做了以下工作:
1. 接收列表数据items和点击监听器listener
2. 实现OnItemClickListener接口,定义了onItemClick()点击方法
3. 在onCreateViewHolder()中使用LayoutInflater加载card_item布局,并返回ViewHolder
4. 在onBindViewHolder()中为每个列表项绑定数据,设置点击监听器调用onItemClick()
5. 获取列表总数getItemCount()
6. 定义内部ViewHolder类,找到布局中的视图并缓存
*/

//创建一个卡片适配器类，用于将数据模型类中的数据绑定到CardView布局中的视图。
class CardAdapter(private val items: List<CardData>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_app_workshop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.iconRes)
        holder.title.text = item.title
        holder.description.text = item.description
        holder.itemView.setOnClickListener { listener.onItemClick(position) }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
    }
}