package com.app.quauhtlemallan.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.activities.MainActivity

class AchievementsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para el fragmento
        return inflater.inflate(R.layout.fragment_achievements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias a los CardViews usando el 'view' inflado
        val cardGeografia = view.findViewById<CardView>(R.id.imageGeografia)
        val cardCultura = view.findViewById<CardView>(R.id.imageCultura)

        // Configurar eventos de clic para redirigir a diferentes pantallas
        cardGeografia.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        cardCultura.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        // Añadir más eventos de clic para las otras categorías
    }
}

