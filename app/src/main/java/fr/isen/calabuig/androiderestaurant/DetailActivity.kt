package fr.isen.calabuig.androiderestaurant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import fr.isen.calabuig.androiderestaurant.databinding.ActivityDetailBinding
import fr.isen.calabuig.androiderestaurant.models.Dish
import kotlin.math.max

class DetailActivity : BaseActivity() {

    companion object {
        const val DISH_EXTRA = "DISH_EXTRA"
        const val ITEMS_COUNT = "ITEMS_COUNT"
        const val USER_PREFERENCES_NAME = "USER_PREFERENCES_NAME"
    }

    lateinit var binding: ActivityDetailBinding
    private var itemCount = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dish = intent.getSerializableExtra(DISH_EXTRA) as? Dish

        dish?.let {
            setupView(it)
        }
    }

    private fun setupView(dish: Dish){
        binding.dishTitleTextView.text = dish.name
        binding.ingredientTextView.text = dish.ingredients.map { it.name }?.joinToString ( ", " )
        binding.viewPager.adapter = PhotoAdapter(this, dish.images)
        refreshShop(dish)

        binding.less.setOnClickListener {
            itemCount = max(1, itemCount - 1)
            refreshShop(dish)
        }

        binding.more.setOnClickListener {
            itemCount += 1
            refreshShop(dish)
        }

        binding.shopButton.setOnClickListener {
            addToBasket(dish, itemCount)
        }
    }

    private fun refreshShop(dish: Dish) {
        val price = itemCount * dish.prices.first().price.toFloat()
        binding.itemCount.text = itemCount.toString()
        binding.shopButton.text = "${"Total"} $price€"
    }

    private fun addToBasket(dish: Dish, count: Int){
        val basket = Basket.getBasket(this)
        basket.addItem(BasketItem(dish, count))
        basket.save(this)
        invalidateOptionsMenu()
        Snackbar.make(binding.root, getString(R.string.basket_validation), Snackbar.LENGTH_LONG).show()
    }
}