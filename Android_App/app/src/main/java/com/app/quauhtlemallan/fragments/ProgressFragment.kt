package com.app.quauhtlemallan.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.adapters.UserAdapter
import com.app.quauhtlemallan.data.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProgressFragment : Fragment() {

    private lateinit var userAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private val userList = mutableListOf<User>()

    private lateinit var userName: TextView
    private lateinit var userLevel: TextView
    private lateinit var userRank: TextView
    private lateinit var profileImage: ImageView
    private lateinit var viewInsigniasButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        activity?.title = "Progreso"

        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowHomeEnabled(false)
            title = "Progreso"
        }

        // Inicializar las vistas de la parte superior
        userName = view.findViewById(R.id.userNameProf)
        userLevel = view.findViewById(R.id.userLevel)
        userRank = view.findViewById(R.id.userRankProf)
        profileImage = view.findViewById(R.id.profileImageProf)
        viewInsigniasButton = view.findViewById(R.id.viewInsigniasButton)

        viewInsigniasButton.setOnClickListener {
            openAchievementsFragment()
        }

        // Inicializar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(context)
        userAdapter = UserAdapter(userList, currentUserEmail)
        recyclerView.adapter = userAdapter

        // Cargar los datos del usuario actual y de la tabla de clasificación
        loadCurrentUser()
        loadUsers()
    }

    private fun openAchievementsFragment() {
        val achievementsFragment = AchievementsFragment()

        // Reemplaza el fragment actual con el de logros
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, achievementsFragment)
            .addToBackStack(null)
            .commit()
    }

    // Cargar información del usuario actual
    private fun loadCurrentUser() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("usuarios")

        usersRef.get().addOnSuccessListener { snapshot ->
            val userList = mutableListOf<User>()
            val level = snapshot.child("level").getValue(Int::class.java) ?: 1
            for (userSnapshot in snapshot.children) {
                val user = userSnapshot.getValue(User::class.java)
                if (user != null) {
                    userList.add(user)
                }
            }

            userList.sortByDescending { it.score }

            val currentUser = userList.find { it.email == currentUserEmail }
            val rank = if (currentUser != null) {
                userList.indexOf(currentUser) + 1
            } else {
                -1
            }

            if (currentUser != null) {
                userName.text = currentUser.username
                userLevel.text = "Nivel $level"
                userRank.text = "Rank #$rank"

                // Cargar la imagen de perfil
                Glide.with(this)
                    .load(currentUser.profileImage ?: R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .error(R.drawable.ic_default)
                    .into(profileImage)
            }
        }
    }

    // Esta función cargará los usuarios desde Firebase y los ordenará por puntaje
    private fun loadUsers() {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("usuarios")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                userList.sortByDescending { it.score }

                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error aquí
                Log.e("FirebaseError", "Error al obtener los datos: ${databaseError.message}")
            }
        })
    }
}
