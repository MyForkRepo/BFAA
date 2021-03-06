package com.pdk.bfaadicoding.submission.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdk.bfaadicoding.submission.data.models.User
import com.pdk.bfaadicoding.submission.databinding.ItemUserListBinding


/**
 * Created by Budi Ardianata on 26/06/2020.
 * Project: BFAAdicoding
 * Email: budiardianata@windowslive.com
 */
class UsersAdapter(private val users:List<User>, private val clickListener: (User , View) -> Unit) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(
            ItemUserListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) = holder.bind(users[position], clickListener)

    override fun getItemCount(): Int = users.size

    inner class UsersViewHolder(private val binding: ItemUserListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind( user: User, click: (User, View) -> Unit) {
            binding.data = user
            binding.root.transitionName = user.username
            binding.root.setOnClickListener { click(user, binding.root) }
        }
    }
}