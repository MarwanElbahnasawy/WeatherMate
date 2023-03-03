package com.example.weathermate.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    lateinit var binding: FragmentFavoritesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFavoritesBinding.inflate(inflater,container,false)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      binding.fabFavorites.setOnClickListener {
          //commented to compile
          //commented to compile
          //commented to compile
          //commented to compile
          //commented to compile
  /*        findNavController().navigate(R.id.navigation_mapsFragmentForFavorites)*/
      }

    }




}