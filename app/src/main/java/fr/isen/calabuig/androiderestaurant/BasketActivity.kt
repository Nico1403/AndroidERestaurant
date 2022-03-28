package fr.isen.calabuig.androiderestaurant

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.isen.calabuig.androiderestaurant.databinding.ActivityBasketBinding
import org.json.JSONObject

class BasketActivity : AppCompatActivity() {
    lateinit var binding: ActivityBasketBinding
    private lateinit var basket: Basket
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reloadData(Basket.getBasket(this))

        binding.orderButton.setOnClickListener {
            val intent=Intent(this, UserActivity::class.java)
            startActivityForResult(intent, UserActivity.REQUEST_CODE)
        }
    }

    private fun reloadData(basket: Basket) {
        binding.basketRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.basketRecyclerView.adapter = BasketAdapter(basket, this) {
            val basket = Basket.getBasket(this)
            val itemToDelete = basket.items.firstOrNull { it.dish.name == it.dish.name }
            basket.items.remove(itemToDelete)
            basket.save(this)
            reloadData(basket)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RegisterActivity.REQUEST_CODE &&
            resultCode  == Activity.RESULT_FIRST_USER) {
            val sharedPreferences = getSharedPreferences((UserActivity.USER_PREFERENCES_NAME), Context.MODE_PRIVATE)
            val idUser = sharedPreferences.getInt(UserActivity.ID_USER, -1)
            if (idUser != -1){
                sendOrder(idUser)
            }
        }
    }

    private fun sendOrder(idUser: Int){
        val message = basket.items.map { "${it.count} x ${it.dish.name}" }.joinToString ("\n")

        val queue = Volley.newRequestQueue(this)
        val url = NetworkConstant.BASE_URL + NetworkConstant.PATH_ORDER

        val jsonData = JSONObject()
        jsonData.put(NetworkConstant.ID_SHOP, "1")
        jsonData.put(NetworkConstant.ID_USER, idUser)
        jsonData.put(NetworkConstant.MSG, message)

        var request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonData,
            { response ->
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Votre commande a bien été prise en compte")
                builder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
                    basket.clear()
                    basket.save(this)
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                builder.show()
            },
            { error ->
                error.message?.let {
                    Log.d("request", it)
                } ?: run {
                    Log.d("request", error.toString())
                    Log.d("request", String(error.networkResponse.data))
                }
            }
        )
        queue.add(request)
    }

    companion object{
        const val REQUEST_CODE = 111
        const val ID_USER = "ID_USER"
        const val USER_PREFERENCES_NAME = "USER_PREFERENCES_NAME"
    }
}