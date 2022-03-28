package fr.isen.calabuig.androiderestaurant

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.calabuig.androiderestaurant.databinding.DishCellBinding
import fr.isen.calabuig.androiderestaurant.models.Dish


class CategoryAdapter(private val entries: List<Dish>,
                      private val entryClickListener: (Dish) -> Unit)
    : RecyclerView.Adapter<CategoryAdapter.DishViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        return DishViewHolder(DishCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = entries[position]
        holder.layout.setOnClickListener {
            entryClickListener.invoke(dish)
        }
        holder.bind(dish)
    }

    override fun getItemCount(): Int {
        return entries.count()
    }

    class DishViewHolder(dishBinding: DishCellBinding): RecyclerView.ViewHolder(dishBinding.root) {
        private val titleView: TextView = dishBinding.dishTitle
        private val priceView: TextView = dishBinding.dishPrice
        private val imageView: ImageView = dishBinding.dishImageView
        val layout = dishBinding.root

        fun bind(dish: Dish) {
            titleView.text = dish.name
            priceView.text = "${dish.prices.first().price} €"
            Picasso.get()
                .load(dish.getThumbnaiUrl())
                .placeholder(R.drawable.lemporter)
                .into(imageView)
        }
    }
}

