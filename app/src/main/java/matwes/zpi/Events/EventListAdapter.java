package matwes.zpi.Events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

import matwes.zpi.Common;
import matwes.zpi.Classes.Event;
import matwes.zpi.EventDetails.EventDetailsActivity;
import matwes.zpi.R;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

/**
 * Created by mateu on 04.04.2017.
 */

class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.Holder>
{
    private Context context;
    private ArrayList<Event> events;

    EventListAdapter(Context context, ArrayList<Event> events)
    {
        this.context = context;
        this.events = events;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position)
    {
        final Event event = events.get(position);


        System.out.println(event.getPlace().getCity());
        holder.title.setText(event.getName());
        holder.date.setText(event.getDateWithTimeString());
        holder.members.setText(event.getMembersStatus());
        holder.place.setText(event.getPlace()!=null ? event.getPlace().getName() : "");
        holder.icon.setImageResource(Common.getIcon(event.getSportName()));
        if(event.getDateWithTime()==null || event.getDateWithTime().before(new Date())){
            holder.changeCardColor();
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventId", event.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class Holder extends RecyclerView.ViewHolder
    {
        TextView title, date, members, place;
        ImageView icon;
        CardView eventCard;

        Holder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvEventName);
            date = (TextView) itemView.findViewById(R.id.tvEventDate);
            members = (TextView) itemView.findViewById(R.id.tvEventMembers);
            place = (TextView) itemView.findViewById(R.id.tvEventPlace);
            icon = (ImageView) itemView.findViewById(R.id.ivEventIcon);
            eventCard = (CardView) itemView.findViewById(R.id.eventCard);
        }

        void changeCardColor()
        {
            eventCard.setBackgroundColor(Color.parseColor("#f44336"));
        }
    }
}
