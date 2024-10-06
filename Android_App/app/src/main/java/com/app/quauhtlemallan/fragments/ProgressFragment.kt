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

        activity?.title = "Progreso"

        // Inicializar las vistas de la parte superior
        userName = view.findViewById(R.id.userName)
        userLevel = view.findViewById(R.id.userLevel)
        userRank = view.findViewById(R.id.userRank)
        profileImage = view.findViewById(R.id.profileImage)
        viewInsigniasButton = view.findViewById(R.id.viewInsigniasButton)

        viewInsigniasButton.setOnClickListener {
            openAchievementsFragment()
        }

        // Inicializar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(context)
        userAdapter = UserAdapter(userList)
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("usuarios").child(userId)

        usersRef.get().addOnSuccessListener { snapshot ->
            val username = snapshot.child("username").getValue(String::class.java) ?: "No disponible"
            val level = snapshot.child("level").getValue(Int::class.java) ?: 1
            val rank = snapshot.child("rank").getValue(Int::class.java) ?: 1
            val profileImageUrl = snapshot.child("profileImage").getValue(String::class.java)

            // Llenar los TextViews
            userName.text = username
            userLevel.text = "Nivel $level"
            userRank.text = "Rank #$rank"

            // Cargar la imagen de perfil
            Glide.with(this)
                .load(profileImageUrl ?: R.drawable.ic_default)
                .placeholder(R.drawable.ic_default)
                .error(R.drawable.ic_default)
                .into(profileImage)
        }
    }

    // Esta función cargará los usuarios desde Firebase y los ordenará por puntaje
    private fun loadUsers() {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("usuarios")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear() // Limpiar la lista antes de cargar nuevos datos
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)  // Añadir cada usuario a la lista
                    }
                }
                Log.i("info", userList.toString())
                // Ordenar los usuarios por puntaje de mayor a menor
                userList.sortByDescending { it.score }

                // Actualizar el adaptador con los nuevos datos
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error aquí
                Log.e("FirebaseError", "Error al obtener los datos: ${databaseError.message}")
            }
        })
    }
}
