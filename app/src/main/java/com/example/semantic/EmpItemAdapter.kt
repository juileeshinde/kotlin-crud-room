package com.example.semantic

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.semantic.databinding.ItemRowBinding

class EmpItemAdapter(val context: Context, val items: ArrayList<EmpModelClass>) :
        RecyclerView.Adapter<EmpItemAdapter.ViewHolder>() {

        /**
         * Inflates the item views which is designed in the XML layout file
         *
         * create a new
         * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
           val binding = ItemRowBinding
               .inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        /**
         * Binds each item in the ArrayList to a view
         *
         * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
         * an item.
         *
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         */
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val item = items.get(position)

            holder.binding.tvName.text = item.name
            holder.binding.tvEmail.text = item.email

            // Updating the background color according to the odd/even positions in list.
            if (position % 2 == 0) {
                holder.llMain.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorLightGray
                    )
                )
            } else {
                holder.llMain.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
            }
            holder.ivEdit.setOnClickListener { view ->

                if (context is AddDepActivity) {
                    //context.updateRecordDialog(item)
                }
            }
            holder.ivDelete.setOnClickListener { view ->

                if (context is AddDepActivity) {
                //    context.deleteRecordAlertDialog(item)
                }
            }
        }

        /**
         * Gets the number of items in the list
         */
        override fun getItemCount(): Int {
            return items.size
        }

        /**
         * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
         */
        class ViewHolder(val binding : ItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
            // Holds the TextView that will add each item to
            val llMain = binding.llMain
            val tvName = binding.tvName
            val tvEmail = binding.tvEmail
            val ivEdit = binding.ivEdit
            val ivDelete = binding.ivDelete
        }
}