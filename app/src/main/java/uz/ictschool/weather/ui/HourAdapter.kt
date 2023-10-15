package uz.ictschool.weather.ui

import android.annotation.SuppressLint
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

class HourAdapter(var hours:JSONArray, var from : Int): RecyclerView.Adapter<HourAdapter.MyHolder>() {
    class MyHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val hour : TextView = itemView.findViewById(R.id.hour)
        val temp : TextView = itemView.findViewById(R.id.temp_hour)
        val icon : ImageView = itemView.findViewById(R.id.img_hour)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(LayoutInflater.from(parent.context).inflate(R.layout.hour_item, parent, false))
    }

    override fun getItemCount(): Int {
        return hours.length()-from
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val hour:JSONObject = hours.getJSONObject(position+from)
        val time = hour.getString("time")
        val temp = hour.getDouble("temp_c")
        val iconUrl = hour.getJSONObject("condition").getString("icon")
        holder.hour.text = time.substring(time.length-6)
        holder.temp.text = temp.toInt().toString() + "℃"
        holder.icon.load("https:$iconUrl")
    }
}