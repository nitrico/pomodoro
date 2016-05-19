package com.github.nitrico.pomodoro.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.TrelloList
import com.thesurix.gesturerecycler.GestureAdapter
import com.thesurix.gesturerecycler.GestureViewHolder
import kotlinx.android.synthetic.main.item_drawer_list.view.*

class DrawerListAdapter() : GestureAdapter<TrelloList, DrawerListAdapter.DrawerViewHolder>() {

    class DrawerViewHolder(val view: View) : GestureViewHolder(view) {
        override fun getDraggableView() = view.dragIcon
        override fun canDrag() = true
        override fun canSwipe() = false
        override fun onItemSelect() { view.setBackgroundResource(R.color.grayTrans) }
        override fun onItemClear() { view.setBackgroundResource(android.R.color.transparent) }
        fun bind(item: TrelloList) { view.name.text = item.name }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drawer_list, parent, false)
        return DrawerViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrawerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(data[position])
    }

}
