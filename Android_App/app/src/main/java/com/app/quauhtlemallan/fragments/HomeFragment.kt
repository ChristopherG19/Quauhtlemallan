package com.app.quauhtlemallan.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.app.quauhtlemallan.R
import com.bumptech.glide.Glide

class HomeFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Inicio"

        val gifImageView = view.findViewById<ImageView>(R.id.gifImageView)

        // Cargar el GIF con Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.mapa)
            .into(gifImageView)
    }

    // Este método se utiliza para crear la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Obtener los argumentos pasados al fragmento
        val email = arguments?.getString("email")
        val provider = arguments?.getString("provider")
        val country = arguments?.getString("country")

        // Configurar el fragmento
        setup(view, email ?: "", provider ?: "")

        // Guardar en SharedPreferences
        val prefs: SharedPreferences.Editor = requireActivity().getSharedPreferences(
            getString(R.string.prefs_file), Context.MODE_PRIVATE
        ).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        return view
    }

    // Método para configurar el título u otros elementos del fragmento
    private fun setup(view: View, email: String, provider: String) {
        requireActivity().title = "Inicio"  // Cambia el título de la actividad que contiene el fragmento
    }

    // Método estático para crear el fragmento y pasar los datos como argumentos
    companion object {
        fun newInstance(email: String, provider: String, country: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("email", email)
            args.putString("provider", provider)
            args.putString("country", country)
            fragment.arguments = args
            return fragment
        }
    }
}
