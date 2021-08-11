package algonquin.cst2335.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChargingStationAdapter extends RecyclerView.Adapter<ChargingStationAdapter.ViewHolder> {

    ArrayList<ChargingStation> stations;
    Context ct;

    public ChargingStationAdapter(Context context, ArrayList<ChargingStation> cs) {
        stations = cs;
        ct = context;
    }

    @NonNull
    @Override
    public ChargingStationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChargingStationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.location.setText("Latitude: "+stations.get(position).get_lat()+" Longitude: "+stations.get(position).get_long());
        holder.address.setText(stations.get(position).getAddress());
        holder.distance.setText("Distance: "+String.valueOf(stations.get(position).get_distance()));

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ct, ChargingStationDetailsActivity.class);
                intent.putExtra("name", stations.get(position).getName());
                intent.putExtra("long", String.valueOf(stations.get(position).get_long()));
                intent.putExtra("lat", String.valueOf(stations.get(position).get_lat()));
                intent.putExtra("phone", stations.get(position).getContact());

                ct.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView location, address, distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            location = (TextView) itemView.findViewById(R.id.location);
            address = (TextView) itemView.findViewById(R.id.address);
            distance = (TextView) itemView.findViewById(R.id.distance);
        }

    }
}
