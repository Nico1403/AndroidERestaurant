package fr.isen.calabuig.androiderestaurant


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    lateinit var spinner : Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        title = "KotlinApp"
        spinner = findViewById(R.id.spinner)
        val fenetre: MutableList<String?> = ArrayList()
        fenetre.add(0, "ACCUEIL")
        fenetre.add("ENTREES")
        fenetre.add("PLATS")
        fenetre.add("DESSERTS")
        val arrayAdapter: ArrayAdapter<String?> = ArrayAdapter<String?>(this, android.R.layout.simple_list_item_1, fenetre)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                var intent: Intent
                if (parent.getItemAtPosition(position) == "Choisissez une fenetre") {
                }
                if (position == 1 ) {
                    intent = Intent(this@HomeActivity, FenetreActivity::class.java)
                    startActivity(intent)
                }
                if (position == 2 ) {
                    intent = Intent(this@HomeActivity, FenetreActivity::class.java)
                    startActivity(intent)
                }
                if (position == 3 ) {
                    intent = Intent(this@HomeActivity, FenetreActivity::class.java)
                    startActivity(intent)
                }
                //else {
                //    val item = parent.getItemAtPosition(position).toString()
                //    Toast.makeText(parent.context, "Selected: $item", Toast.LENGTH_SHORT).show()
                  //  startActivity()
                //}
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeActivity","L'activité est détruite")
    }
}