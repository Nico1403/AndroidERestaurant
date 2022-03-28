package fr.isen.calabuig.androiderestaurant

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import fr.isen.calabuig.androiderestaurant.databinding.ActivityRegisterBinding
import fr.isen.calabuig.androiderestaurant.models.RegisterResult
import fr.isen.calabuig.androiderestaurant.models.User
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRegister.setOnClickListener {
            if(verifyInformation()){
                launchRequest()
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show()
            }
        }

        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun launchRequest(){
        val queue = Volley.newRequestQueue(this)
        val url = NetworkConstant.BASE_URL + NetworkConstant.PATH_REGISTER

        val loader = Loader()
        loader.show(this, "Création du client en cours")

        val jsondata = JSONObject()
        jsondata.put(NetworkConstant.ID_SHOP, "1")
        jsondata.put(NetworkConstant.EMAIL, binding.email.text)
        jsondata.put(NetworkConstant.PASSWORD, binding.password.text)
        jsondata.put(NetworkConstant.FIRSTNAME, binding.firstname.text)
        jsondata.put(NetworkConstant.LASTNAME, binding.lastname.text)

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsondata,
            {response ->
                loader.hide(this)
                val userResult = GsonBuilder().create().fromJson(response.toString(), RegisterResult::class.java)
                saveUser(userResult.data)
            },
            {error ->
                loader.hide(this)
                error.message?.let {
                    Log.d("request", it)
                }?: run {
                    Log.d("request", error.toString())
                    Log.d("request", String(error.networkResponse.data))
                }
            }
        )
        queue.add(request)
    }

    private fun verifyInformation(): Boolean {
        return (binding.email.text?.isNotEmpty() == true ||
                binding.firstname.text?.isNotEmpty() == true ||
                binding.lastname.text?.isNotEmpty() == true ||
                binding.password.text?.count()?: 0 >= 6)
    }

    fun saveUser(user: User){
        val sharedPreferences = getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(ID_USER, user.id)
        editor.apply()

        setResult(Activity.RESULT_FIRST_USER)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE &&
                resultCode  == Activity.RESULT_FIRST_USER) {
            setResult(Activity.RESULT_FIRST_USER)
            finish()
        }
    }

    companion object{
        const val REQUEST_CODE = 111
        const val ID_USER = "ID_USER"
        const val USER_PREFERENCES_NAME = "USER_PREFERENCES_NAME"
    }
}