package codelab.android.sepomex
import android.R
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import codelab.android.sepomex.databinding.ActivityMainBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var volleyAPI: VolleyApi
    private val API_URL="192.168.0.254:8080"
    private val estados = ArrayList<Estado>()
    private val items = arrayListOf("Selecciona un estado")
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var spinner: Spinner
    private lateinit var estadoSeleccionado: Estado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getEstados()
        spinner=binding.spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    estadoSeleccionado = estados[position - 1]
                    Toast.makeText(applicationContext, "Seleccionado: ${estadoSeleccionado.nombre}", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext, "Ningún estado seleccionado", Toast.LENGTH_SHORT).show()
                    estadoSeleccionado = Estado(-1, "")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinner.setSelection(0)
                Toast.makeText(applicationContext, "Selecciona un estado", Toast.LENGTH_SHORT).show()
            }
        }

        binding.button.setOnClickListener {
            postMunicipio()
        }
    }

    private fun postMunicipio(){
        if(estadoSeleccionado.id == -1){
            Toast.makeText(this, "Selecciona un estado", Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.editText.text.isEmpty()){
            Toast.makeText(this, "Ingresa un nombre", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://$API_URL/agregarmunicipio"
        volleyAPI = VolleyApi.getInstance(this)
        val jsonEstado = JSONObject().apply {
            put("id_ets", estadoSeleccionado.id)
            put("estados", estadoSeleccionado.nombre)
        }
        val jsonBody = JSONObject().apply {
            put("nombre", binding.editText.text.toString() )
            put("estado", jsonEstado)
        }

        Log.d("POST", jsonBody.toString())

        volleyAPI.add(object: JsonObjectRequest(
            Method.POST,
            url,
            jsonBody,
            Response.Listener {response ->
                Toast.makeText(this, "Municipio agregado", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Log.d("POSTERROR", error.message.toString())
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }
        ){
            override fun getBodyContentType(): String {
                return "application/json"
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Windows NT 6.1)"
                return headers
            }
        })
    }

    private fun getEstados(){
        val url = "http://$API_URL/estados"
        volleyAPI = VolleyApi.getInstance(this)
        volleyAPI.add(JsonArrayRequest(
            url,
            { response ->
                for (i in 0 until response.length()) {
                    val estado = response.getJSONObject(i)
                    val id = estado.getInt("id_ets")
                    val nombre = estado.getString("estados")
                    estados.add(Estado(id, nombre))
                }
                items.addAll(estados.map { it.nombre })
                adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
                spinner.adapter = adapter
                spinner.setSelection(0)
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ))
    }
}