package fr.isen.calabuig.androiderestaurant


import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo.getCategoryTitle
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient.FileChooserParams.parseResult
import androidx.core.provider.FontsContractCompat.resetCache
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import fr.isen.calabuig.androiderestaurant.databinding.ActivityCategoryBinding
import fr.isen.calabuig.androiderestaurant.models.Dish
import org.json.JSONObject
import fr.isen.calabuig.androiderestaurant.models.MenuResult

enum class ItemType {
    STARTER, MAIN, DESSERT;

    companion object {
        fun categoryTitle(item: ItemType?): String {
            return when (item) {
                STARTER -> "Entrées"
                MAIN -> "Plats"
                DESSERT -> "Desserts"
                else -> ""
            }
        }
    }
}

class CategoryActivity : BaseActivity() {
    private lateinit var binding: ActivityCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedItem = intent.getSerializableExtra(HomeActivity.CATEGORY_NAME) as? ItemType

        binding.swipeLayout.setOnRefreshListener {
            resetCache()
            makeRequest(selectedItem)
        }

        binding.categoryTitle.text = getCategoryTitle(selectedItem)

        loadList(listOf<Dish>())

        makeRequest(selectedItem)

        Log.d("lifecycle", "onCreate")
    }

    private fun makeRequest(selectedItem: ItemType?) {
        resultFromCache()?.let {
            parseResult(it, selectedItem)
        }?: run {
            val loader = Loader()
            loader.show(this, "récupération du menu")
            val queue = Volley.newRequestQueue(this)
            val url = NetworkConstant.BASE_URL + NetworkConstant.PATH_MENU
            val jsondata = JSONObject()
            jsondata.put("id_shop", 1)
            var stringRequest = JsonObjectRequest(
                Request.Method.POST,
                url,
                jsondata,
                { response ->
                    loader.hide(this)
                    binding.swipeLayout.isRefreshing = false
                    cacheResult(response.toString())
                    parseResult(response.toString(), selectedItem)
                },
                { error ->
                    loader.hide(this)
                    binding.swipeLayout.isRefreshing = false
                    error.message?.let {
                        Log.d("request", it)
                    } ?: run {
                        Log.d("request", error.toString())
                    }
                }
            )
            queue.add(stringRequest)
        }
    }

    private fun cacheResult (response: String) {
        val sharedPreferences = getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(REQUEST_CACHE, response)
        editor.apply()
    }

    private fun resetCache() {
        val sharedPreferences = getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(REQUEST_CACHE)
        editor.apply()
    }

    private fun resultFromCache(): String? {
        val sharedPreferences = getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(REQUEST_CACHE, null)
    }

    private fun parseResult(response: String, selectedItem: ItemType?) {
        val menuResult = GsonBuilder().create().fromJson(response, MenuResult::class.java)
        val items = menuResult.data.firstOrNull { it.name == ItemType.categoryTitle(selectedItem) }
        loadList(items?.items)
    }

    private fun loadList (items: List<Dish>?){
        items?.let {
            val adapter = CategoryAdapter(it) {dish ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra(DetailActivity.DISH_EXTRA, dish)
                startActivity(intent)
            }
            binding.recyclerView.layoutManager= LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }
    }



    private fun getCategoryTitle(item: ItemType?): String {
        return when(item) {
            ItemType.STARTER -> getString(R.string.starter)
            ItemType.MAIN -> getString(R.string.main)
            ItemType.DESSERT -> getString(R.string.dessert)
            else -> ""
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifecycle", "onResume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("lifecycle", "onRestart")
    }

    override fun onDestroy() {
        Log.d("lifecycle", "onDestroy")
        super.onDestroy()
    }

    companion object {
        const val USER_PREFERENCES_NAME = "USER_PREFERENCES_NAME"
        const val REQUEST_CACHE = "REQUEST_CACHE"
    }
}