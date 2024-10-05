package com.app.quauhtlemallan.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProgressFragment : Fragment() {

    private lateinit var userAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private val userList = mutableListOf<User>()

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

        // Inicializar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Inicializar el adaptador con una lista vacía y luego actualizarla
        userAdapter = UserAdapter(userList)
        recyclerView.adapter = userAdapter

        // Llamar a la función para cargar los datos desde Firebase
        loadUsers()
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
