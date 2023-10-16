package uz.ictschool.weather.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import uz.ictschool.weather.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val apiUrl = "https://api.weatherapi.com/v1/forecast.json?key=11b9394e7e024a2588a44954230610&q=Tashkent&days=8&aqi=no&alerts=no"
    var forecastAdapter = ForecastAdapter(JSONArray(), object : ForecastAdapter.ItemClickInterface{
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onParentClick(day: JSONObject, position: Int) {
            changeToday(day, position)
        }
    })
    var todayAdapter = HourAdapter(JSONArray(), 0)
    @RequiresApi(Build.VERSION_CODES.O)
    var fromHour = LocalTime.now().hour
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recForecast.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recHours.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val parsedDate = LocalDateTime.parse(LocalDateTime.now().toString(), DateTimeFormatter.ISO_DATE_TIME)
        val formattedDate = parsedDate.format(DateTimeFormatter.ISO_DATE)

        binding.currentDate.text = formattedDate

        getAPI()

        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing = false
            val parsedDate = LocalDateTime.parse(LocalDateTime.now().toString(), DateTimeFormatter.ISO_DATE_TIME)
            val formattedDate = parsedDate.format(DateTimeFormatter.ISO_DATE)

            binding.currentDate.text = formattedDate
            getAPI()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    fun changeToday(day: JSONObject, position:Int){
        if (position == 0){
            binding.date.text = "Today"
            fromHour = LocalTime.now().hour
        }else{
            binding.date.text = day.getString("date")
            fromHour = 0
        }
        todayAdapter.hours = day.getJSONArray("hour")
        todayAdapter.from = fromHour
        todayAdapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAPI() {
        val requestQue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(apiUrl,
            { response ->
                val current = response.getJSONObject("current")
                val tempC = current.getDouble("temp_c")
                val windKph = current.getDouble("wind_kph")
                val humidity = current.getInt("humidity")
                val precip = current.getDouble("precip_in")

                binding.shamol.text = "${windKph}km/h"
                binding.namlik.text = "${humidity}%"
                binding.currentTemp.text = "${tempC.toInt()}"
                binding.yomgir.text = precip.toInt().toString() + "%"

                forecastAdapter = ForecastAdapter(response.getJSONObject("forecast").getJSONArray("forecastday"), object : ForecastAdapter.ItemClickInterface{
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onParentClick(day: JSONObject, position: Int) {
                        changeToday(day, position)
                    }
                })
                binding.recForecast.adapter = forecastAdapter
                todayAdapter = HourAdapter(response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour"), fromHour)
                binding.recHours.adapter = todayAdapter
                binding.mainIcon.load("https:" + current.getJSONObject("condition").getString("icon"))
                forecastAdapter.notifyDataSetChanged()
            }
        ) { error -> Log.d("TAG", "onErrorResponse: $error") }
        requestQue.add(request)
    }
}