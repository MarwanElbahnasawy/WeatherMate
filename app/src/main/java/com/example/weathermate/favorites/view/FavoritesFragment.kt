package com.example.weathermate.favorites.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.MyApp
import com.example.weathermate.databinding.FragmentFavoritesBinding
import com.example.weathermate.home.view.DailyAdapter
import com.example.weathermate.model.FavoriteAddress

class FavoritesFragment : Fragment() , OnItemClickFavorites {

    lateinit var binding: FragmentFavoritesBinding

    lateinit var favoritesAdapter: FavoritesAdapter

    lateinit var sharedPreferences : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFrag()

        activateFABFavorites()

        setupFavoritesAdapter()





    }

    private fun initFrag() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

    }



    private fun activateFABFavorites() {
        binding.fabFavorites.setOnClickListener {
            findNavController().navigate(FavoritesFragmentDirections.actionNavigationFavoritesToMapFragment(isFromFavorites = true))
        }
    }

    private fun setupFavoritesAdapter() {
        val mlayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        favoritesAdapter = FavoritesAdapter(this, lifecycleScope)

        binding.rvFavorites.apply {
            layoutManager = mlayoutManager
            adapter = favoritesAdapter
        }

        MyApp.getInstanceRepository().getAllFavoriteAddresses().observe(viewLifecycleOwner){
            favoritesAdapter.submitList(it)
        }


    }

    override fun onItemClickFavorites(favoriteAddress: FavoriteAddress) {
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString("latitude", favoriteAddress.latitude.toString())
        sharedPreferencesEditor.putString("longitude", favoriteAddress.longitude.toString())
        sharedPreferencesEditor.apply()
        findNavController().navigate(FavoritesFragmentDirections.actionNavigationFavoritesToNavigationHome())
    }


}