package com.example.weathermate.favorites.view
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.MyApp
import com.example.weathermate.databinding.ItemFavoriteBinding
import com.example.weathermate.model.FavoriteAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavoritesAdapter (private val onItemClickFavorites: OnItemClickFavorites,
                        private val lifeCycleScopeInput: CoroutineScope
)
    : ListAdapter<FavoriteAddress, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffUtil()){


    class FavoriteViewHolder(var binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {}

class FavoriteDiffUtil : DiffUtil.ItemCallback<FavoriteAddress>() {
    override fun areItemsTheSame(oldItem: FavoriteAddress, newItem: FavoriteAddress): Boolean {
        return oldItem.latlon == newItem.latlon
    }

    override fun areContentsTheSame(oldItem: FavoriteAddress, newItem: FavoriteAddress): Boolean {
        return oldItem == newItem
    }

}

    lateinit var binding: ItemFavoriteBinding

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(parent.context)

        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemFavoriteBinding.inflate(inflater, parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val current = getItem(position)

        holder.binding.tvAddressName.text = current.address

        holder.binding.imgDeleteFavorite.setOnClickListener {
            lifeCycleScopeInput.launch {
                MyApp.getInstanceRepository().deleteFavoriteAddress(current)
            }

        }

        holder.binding.root.setOnClickListener {
            onItemClickFavorites.onItemClickFavorites(current)
        }

    }

}