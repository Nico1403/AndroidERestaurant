package fr.isen.calabuig.androiderestaurant

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.calabuig.androiderestaurant.databinding.BasketCellBinding
import fr.isen.calabuig.androiderestaurant.models.Dish

class BasketAdapter(private val basket: Basket,
                    private val context: Context,
                    private val entryClickListener: (BasketItem) -> Unit): RecyclerView.Adapter<BasketAdapter.BasketViewHolder>() {

    class BasketViewHolder(binding: BasketCellBinding): RecyclerView.ViewHolder(binding.root) {
        private val itemTitle: TextView = binding.basketItemTitle
        private val itemPrice: TextView = binding.basketItemPrice
        private val itemQuantity: TextView = binding.basketItemQuantity
        private val itemImageView: ImageView = binding.basketItemImageView
        private val deleteButton: ImageView = binding.basketItemDelete
        val layout = binding.root

        fun bind(item: BasketItem, context: Context, entryClickListener: (BasketItem) -> Unit/*, delegate: BasketCellInterface*/) {
            itemTitle.text = item.dish.name
            itemPrice.text = "${item.dish.prices.first().price}€"
            itemQuantity.text = "${context.getString(R.string.quantity)} ${item.count.toString()}"
            Picasso.get()
                    .load(item.dish.getThumbnaiUrl())
                    .placeholder(R.drawable.android_logo_restaurant)
                    .into(itemImageView)
            deleteButton.setOnClickListener{
                entryClickListener.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        return BasketViewHolder(BasketCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return basket.items.count()
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        val item = basket.items[position]
        holder.layout.setOnClickListener {
            entryClickListener.invoke(item)
        }
        holder.bind(item, context, entryClickListener)
    }
}