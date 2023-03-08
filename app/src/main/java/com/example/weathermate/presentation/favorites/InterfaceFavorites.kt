package com.example.weathermate.presentation.favorites

import com.example.weathermate.data.model.FavoriteAddress

interface InterfaceFavorites {
    fun onItemClickFavorites(favoriteAddress: FavoriteAddress)
    fun onDeleteClickFavorites(favoriteAddress: FavoriteAddress)
}