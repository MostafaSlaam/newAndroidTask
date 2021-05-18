package android.task.adapter

import android.task.R
import android.task.databinding.ItemRecyclerWordBinding
import android.task.model.WordModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shababit.observer.OnRecyclerItemClickListener


class RecyclerWordsAdapter(
    var items: ArrayList<WordModel>, var listener: OnRecyclerItemClickListener
) : RecyclerView.Adapter<RecyclerWordsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_recycler_word,
            parent,
            false
        ) as ItemRecyclerWordBinding
        return RecyclerWordsAdapter.ViewHolder(binding)
    }

    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item =items[position]
        holder.binding.model = item
        holder.binding.root.setOnClickListener {
        }
    }

    fun setList(list: ArrayList<WordModel>) {
        this.items = list
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemRecyclerWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


}