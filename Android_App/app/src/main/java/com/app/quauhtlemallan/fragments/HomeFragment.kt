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
import com.app.quauhtlemallan.data.User
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

        // Obtener el objeto User pasado al fragmento como argumento
        val user = arguments?.getSerializable("user") as? User
        val provider = arguments?.getString("provider")

        // Configurar el fragmento
        user?.let {
            setup(view, it)
        }

        // Guardar los datos en SharedPreferences (si es necesario)
        val prefs: SharedPreferences.Editor = requireActivity().getSharedPreferences(
            getString(R.string.prefs_file), Context.MODE_PRIVATE
        ).edit()
        prefs.putString("email", user?.email)
        prefs.putString("provider", provider)
        prefs.apply()

        return view
    }

    private fun setup(view: View, user: User) {
        requireActivity().title = "Inicio"
    }

    // Método estático para crear el fragmento y pasar los datos como argumentos
    companion object {
        fun newInstance(user: User): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putSerializable("user", user)
            fragment.arguments = args
            return fragment
        }
    }
}
