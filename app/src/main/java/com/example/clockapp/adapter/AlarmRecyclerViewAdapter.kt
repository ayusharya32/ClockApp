package com.example.clockapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.clockapp.R
import com.example.clockapp.db.entities.Alarm
import com.example.clockapp.util.Utilities
import kotlinx.android.synthetic.main.alarm_item.view.*

class AlarmRecyclerViewAdapter(

): RecyclerView.Adapter<AlarmRecyclerViewAdapter.AlarmViewHolder>(){

    inner class AlarmViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    val diffCallBack =
        object : DiffUtil.ItemCallback<Alarm>(){
            override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }

    val differ = AsyncListDiffer<Alarm>(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        return AlarmViewHolder(view)
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {

        val currentItem = differ.currentList[position]
        val alarmTime = Utilities.getAlarmTime(currentItem)

        holder.itemView.tvTime.text = alarmTime

        holder.itemView.smAlarmToggle.isChecked = currentItem.isAlarmEnabled!!
        updateAlarmTextColor(holder)

        holder.itemView.smAlarmToggle.setOnClickListener {
            onToggleClickListener?.let {
                    it(currentItem)
            }
            updateAlarmTextColor(holder)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(currentItem)
            }
        }

    }

    private fun updateAlarmTextColor(holder: AlarmViewHolder){
        if(holder.itemView.smAlarmToggle.isChecked){
            holder.itemView.apply {
                tvTime.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvAlarm.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        } else {
            holder.itemView.apply {
                tvTime.setTextColor(ContextCompat.getColor(context, R.color.grey))
                tvAlarm.setTextColor(ContextCompat.getColor(context, R.color.grey))
            }
        }
    }

    private var onItemClickListener: ((Alarm) -> Unit)? = null
    private var onToggleClickListener: ((Alarm) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Alarm) -> Unit) ){
        onItemClickListener = listener
    }

    fun setOnToggleClickListener(listener: ((Alarm) -> Unit) ){
        onToggleClickListener = listener
    }
}
