package com.meta_engine.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewholder(v: View) : RecyclerView.ViewHolder(v) {
    abstract fun bind(position: Int)
}