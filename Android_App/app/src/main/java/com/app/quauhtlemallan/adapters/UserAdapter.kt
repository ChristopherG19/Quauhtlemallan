package com.app.quauhtlemallan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.data.User
import com.bumptech.glide.Glide

class UserAdapter(private val users: MutableList<User>, private val currentUserEmail: String) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val userRank: TextView = itemView.findViewById(R.id.userRank)
        val userScore: TextView = itemView.findViewById(R.id.userScore)
        val cardView: CardView  = itemView.findViewById(R.id.cardViewUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.userRank.text = (position + 1).toString()
        holder.userScore.text = "Total de puntos: ${user.score}"

        Glide.with(holder.itemView.context)
            .load(user.profileImage)
            .placeholder(R.drawable.ic_default)
            .into(holder.userImage)

        holder.userName.text = user.username

        if (user.email == currentUserEmail) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.ranks))
        }
    }

    override fun getItemCount() = users.size

    fun updateUserList(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }
}

