package android.task.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView



class CustomViewBindings {
    companion object {
        @BindingAdapter("adapter")
        @JvmStatic
        fun bindRecyclerViewAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
            recyclerView.adapter = adapter
        }

    }
}