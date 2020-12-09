package mx.udg.cuvalles.volley

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    var ayuda:FloatingActionButton?=null
    var adaptadorPaises:PaisesAdapter?=null
    var listaPaises:ArrayList<Pais>?=null

    var miRecyclerCovid:RecyclerView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ayuda = findViewById(R.id.btnAyuda)

        ayuda!!.setOnClickListener {
            val i = Intent(this,Info::class.java)
            startActivity(i)
        }
        miRecyclerCovid = findViewById(R.id.myRecyclerCovid)

        listaPaises = ArrayList<Pais>()

        adaptadorPaises = PaisesAdapter(listaPaises!!,this)

        miRecyclerCovid!!.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        miRecyclerCovid!!.adapter = adaptadorPaises

        val queue = Volley.newRequestQueue(this)
        val url = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/latest"

        val peticionDatosCovid = JsonArrayRequest(Request.Method.GET,url,null,Response.Listener { response ->
            for (index in 0..response.length()-1){
                try {
                    val paisJson = response.getJSONObject(index)
                    val nombrePais = paisJson.getString("countryregion")
                    val numeroConfirmados = paisJson.getInt("confirmed")
                    val numeroMuertos = paisJson.getInt("deaths")
                    val numeroRecuperados = paisJson.getInt("recovered")
                    val countryCodeJson = paisJson.getJSONObject("countrycode")
                    val codigoPais = countryCodeJson.getString("iso2")
                    //val nombreP = paisJson.getString("countryregion")
                    //objeto de kotlin
                    val paisIndividual = Pais(nombrePais,numeroConfirmados,numeroMuertos,numeroRecuperados,codigoPais)
                    listaPaises!!.add(paisIndividual)
                }catch (e:JSONException){
                    Log.wtf("JsonError",e.localizedMessage)
                }
            }

            listaPaises!!.sortByDescending { it.confirmados }
            adaptadorPaises!!.notifyDataSetChanged()
        },
        Response.ErrorListener { error ->
            Log.e("error_volley",error.localizedMessage)
        })
        queue.add(peticionDatosCovid)
    }
}