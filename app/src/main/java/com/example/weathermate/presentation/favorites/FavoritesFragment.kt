package com.example.weathermate.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.MyApp
import com.example.weathermate.databinding.FragmentFavoritesBinding
import com.example.weathermate.data.model.FavoriteAddress
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() , InterfaceFavorites {

    lateinit var binding: FragmentFavoritesBinding
    lateinit var favoritesAdapter: FavoritesAdapter
    lateinit var favoritesViewModel: FavoritesViewModel
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
        val favoritesViewModelFactory = FavoritesViewModelFactory(MyApp.getInstanceRepository())
        favoritesViewModel = ViewModelProvider(this, favoritesViewModelFactory)[FavoritesViewModel::class.java]
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

        favoritesViewModel.getAllFavoriteAddresses().observe(viewLifecycleOwner){
            favoritesAdapter.submitList(it)
        }
    }

    override fun onItemClickFavorites(favoriteAddress: FavoriteAddress) {
        favoritesViewModel.putStringInSharedPreferences("latitude", favoriteAddress.latitude.toString())
        favoritesViewModel.putStringInSharedPreferences("longitude", favoriteAddress.longitude.toString())

        findNavController().navigate(FavoritesFragmentDirections.actionNavigationFavoritesToNavigationHome())
    }

    override fun onDeleteClickFavorites(favoriteAddress: FavoriteAddress) {
        lifecycleScope.launch {
            favoritesViewModel.deleteFavoriteAddress(favoriteAddress)
        }
    }
}