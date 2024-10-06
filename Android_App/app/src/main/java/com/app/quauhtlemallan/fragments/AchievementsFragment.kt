package com.app.quauhtlemallan.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.activities.MainActivity
import com.app.quauhtlemallan.adapters.CategoryAdapter
import com.app.quauhtlemallan.data.Category

class AchievementsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private val categoryList = mutableListOf<Category>() // Aquí defines las categorías

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para el fragmento
        return inflater.inflate(R.layout.fragment_achievements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Insignias"

        // Inicializar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewCategories)
        recyclerView.layoutManager = GridLayoutManager(context, 3) // 3 columnas

        // Inicializar el adaptador con una lista de categorías (Category sería una data class)
        categoryAdapter = CategoryAdapter(categoryList)
        recyclerView.adapter = categoryAdapter

        // Llenar la lista de categorías
        loadCategories()
    }

    private fun loadCategories() {
        // Aquí agregas las categorías que necesitas mostrar
        categoryList.add(Category("Geografía", R.drawable.ic_home))
        categoryList.add(Category("Cultura", R.drawable.ic_home))
        categoryList.add(Category("Turismo", R.drawable.ic_home))
        categoryList.add(Category("Idiomas", R.drawable.ic_home))
        categoryList.add(Category("Gastronomía", R.drawable.ic_home))
        categoryList.add(Category("Historia y Política", R.drawable.ic_home))
        categoryList.add(Category("Artes", R.drawable.ic_home))
        categoryList.add(Category("Deportes", R.drawable.ic_home))

        // Notificar al adaptador que los datos han cambiado
        categoryAdapter.notifyDataSetChanged()
    }
}


