package com.example.tlu_routine.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlu_routine.R;
import com.example.tlu_routine.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> events = new ArrayList<>();
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);

        void onEventCheckedChange(Event event, boolean isChecked);
    }

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.listener = listener;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEventTime;
        private TextView tvEventName;
        private CheckBox cbEventDone;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventTime = itemView.findViewById(R.id.tv_event_time);
            tvEventName = itemView.findViewById(R.id.tv_event_name);
            cbEventDone = itemView.findViewById(R.id.cb_event_done);
        }

        public void bind(Event event) {
            // Set time range
            String timeRange = event.getStartTime() + " - " + event.getEndTime();
            tvEventTime.setText(timeRange);

            // Set event name
            tvEventName.setText(event.getName());

            // Set checkbox state from event
            cbEventDone.setOnCheckedChangeListener(null); // Remove listener before setting
            cbEventDone.setChecked(event.isCompleted());

            // Apply strike-through if completed
            if (event.isCompleted()) {
                tvEventName.setPaintFlags(tvEventName.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                tvEventTime.setAlpha(0.6f);
                tvEventName.setAlpha(0.6f);
            } else {
                tvEventName
                        .setPaintFlags(tvEventName.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
                tvEventTime.setAlpha(1.0f);
                tvEventName.setAlpha(1.0f);
            }

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            });

            cbEventDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null && buttonView.isPressed()) {
                    listener.onEventCheckedChange(event, isChecked);
                }
            });
        }
    }
}