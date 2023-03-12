package com.example.weathermate.presentation.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.weathermate.MyApp
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentFavoritesBinding
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.remote.RetrofitStateFavorites
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.joery.animatedbottombar.AnimatedBottomBar

class FavoritesFragment : Fragment() , InterfaceFavorites {

    private val TAG = "commonnn"

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
        loadFavorites()

        binding.swipeRefreshLayout.setOnRefreshListener {
            findNavController().navigate(R.id.navigation_favorites)
        }
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

    private fun loadFavorites() {
        lifecycleScope.launch {

            favoritesViewModel.observeFavorites().observe(viewLifecycleOwner){
                favoritesViewModel.reloadFavoritesOnline(it)
            }


            favoritesViewModel.retrofitStateFavorites.collectLatest {
                when (it) {
                    is RetrofitStateFavorites.Loading -> {
                        binding.imgLoading.visibility = View.VISIBLE
                        startLottieAnimation(binding.imgLoading, "loading.json")
                        binding.constraintLayoutFavorite.visibility = View.GONE
                    }
                    is RetrofitStateFavorites.OnSuccess -> {
                            updateUi(it.listFavoriteAddresses)
                            binding.constraintLayoutFavorite.visibility = View.VISIBLE
                            binding.imgLoading.visibility = View.GONE
                            binding.imgLoading.pauseAnimation()
                    }
                    is RetrofitStateFavorites.OnFail -> {
                        Log.i(TAG, it.errorMessage.toString())
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateUi(listFavoriteAddresses: List<FavoriteAddress>) {

        Log.i(TAG, "updateUi111111: " + listFavoriteAddresses.size)

        val mlayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        favoritesAdapter = FavoritesAdapter(this, lifecycleScope)

        binding.rvFavorites.apply {
            layoutManager = mlayoutManager
            adapter = favoritesAdapter
        }

        favoritesAdapter.submitList(listFavoriteAddresses)

    }

    override fun onItemClickFavorites(favoriteAddress: FavoriteAddress) {
        favoritesViewModel.putStringInSharedPreferences("latitude", favoriteAddress.latitude.toString())
        favoritesViewModel.putStringInSharedPreferences("longitude", favoriteAddress.longitude.toString())

        findNavController().navigate(FavoritesFragmentDirections.actionNavigationFavoritesToNavigationHome())

        val bottomNavView = requireActivity().findViewById<AnimatedBottomBar>(R.id.bottom_nav_view)
        bottomNavView.selectTabAt(0,true)
    }

    override fun onDeleteClickFavorites(favoriteAddress: FavoriteAddress) {
        lifecycleScope.launch {
            favoritesViewModel.deleteFavoriteAddress(favoriteAddress)
        }
    }

    private fun startLottieAnimation(animationView: LottieAnimationView, animationName: String) {
        animationView.setAnimation(animationName)
        animationView.repeatCount = LottieDrawable.INFINITE
        animationView.repeatMode = LottieDrawable.RESTART
        animationView.playAnimation()
    }
}