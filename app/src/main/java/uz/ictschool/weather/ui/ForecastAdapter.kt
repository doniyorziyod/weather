package uz.ictschool.weather.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.json.JSONArray
import org.json.JSONObject
import uz.ictschool.weather.R

class ForecastAdapter(private val days:JSONArray, val itemClickInterface: ItemClickInterface): RecyclerView.Adapter<ForecastAdapter.MyHolder>() {
    class MyHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val day:TextView = itemView.findViewById(R.id.forecast_date)
        val temp:TextView = itemView.findViewById(R.id.forecast_temp)
        val icon:ImageView = itemView.findViewById(R.id.forecast_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(LayoutInflater.from(parent.context).inflate(R.layout.forecast_item, parent, false))
    }

    override fun getItemCount(): Int {
        return days.length()
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val day = days.getJSONObject(position)
        val date = day.getString("date")
        val temp = day.getJSONObject("day").getDouble("avgtemp_c")
        val iconUrl = day.getJSONObject("day").getJSONObject("condition").getString("icon")

        holder.day.text = date
        holder.temp.text = temp.toInt().toString() + "℃"
        holder.icon.load("https:" + iconUrl)
        holder.itemView.setOnClickListener {
            itemClickInterface.onParentClick(day, position)
        }
    }
    interface ItemClickInterface{
        fun onParentClick(day : JSONObject, position: Int)
    }
}