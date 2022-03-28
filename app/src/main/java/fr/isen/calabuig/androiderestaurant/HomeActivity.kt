package fr.isen.calabuig.androiderestaurant


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.isen.calabuig.androiderestaurant.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.starter.setOnClickListener {
            startCategoryActivity(ItemType.STARTER)
        }

        binding.main.setOnClickListener {
            startCategoryActivity(ItemType.MAIN)
        }

        binding.dessert.setOnClickListener {
            startCategoryActivity(ItemType.DESSERT)
        }
    }

    private fun startCategoryActivity(item: ItemType) {
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra(CATEGORY_NAME, item)
        startActivity(intent)
    }

    companion object {
        const val CATEGORY_NAME = "CATEGORY_NAME"
    }
}