package com.example.weathermate.presentation.splash

import android.animation.Animator
import android.animation.ValueAnimator
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ViewAnimator
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.weathermate.MyApp
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentSplashBinding
import com.example.weathermate.util.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    lateinit var splashViewModel: SplashViewModel
    private lateinit var binding: FragmentSplashBinding



    private fun changeThemeOnStartup() {
        if (splashViewModel.isPreferencesSet()) {
            if (splashViewModel.isDark()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        playSplashAnimation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModelFactory = SplashViewModelFactory(MyApp.getInstanceRepository())
        splashViewModel = ViewModelProvider(this,viewModelFactory)[SplashViewModel::class.java]



        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeThemeOnStartup()
    }

    private fun playSplashAnimation() {
        val animationView = binding.imgSplash
        animationView.setAnimation("splash.json")
        animationView.playAnimation()

        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                if (splashViewModel.isPreferencesSet()) {
                    val action =
                        SplashFragmentDirections.actionSplashFragmentToNavigationHome()
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build()

                    if(isAdded)
                    findNavController().navigate(action, navOptions)

                } else{
                    val action =
                        SplashFragmentDirections.actionSplashFragmentToNavigationPreferences()
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build()

                    val toast = Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.internetDisconnectedFirstTimeInApp),
                        Toast.LENGTH_LONG
                    )

                    lifecycleScope.launch(Dispatchers.Main) {
                        while (true) {
                            if (NetworkManager.isInternetConnected()) {
                                toast.cancel()

                                if(isAdded)
                                findNavController().navigate(action, navOptions)
                                break
                            } else {
                                toast.show()
                            }
                            delay(1000)
                        }
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
}