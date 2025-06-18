package com.example.tlu_routine.utils;

import android.content.Context;
import android.util.Log;

import com.example.tlu_routine.model.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventJsonManager {
    private static final String TAG = "EventJsonManager";
    private static final String FILENAME = "events.json";

    private final Context context;
    private final Gson gson;

    public EventJsonManager(Context context) {
        this.context = context;

        // Configure Gson with LocalDate adapter
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                        (JsonSerializer<LocalDate>) (src, typeOfSrc,
                                context1) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .registerTypeAdapter(LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, typeOfT, context1) -> LocalDate.parse(json.getAsString(),
                                DateTimeFormatter.ISO_LOCAL_DATE))
                .setPrettyPrinting()
                .create();
    }

    private File getEventsFile() {
        return new File(context.getFilesDir(), FILENAME);
    }

    public void saveEvent(Event event) {
        List<Event> events = loadEvents();
        events.add(event);
        saveEvents(events);
    }

    public void updateEvent(Event event) {
        List<Event> events = loadEvents();
        boolean updated = false;

        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(event.getId())) {
                events.set(i, event);
                updated = true;
                break;
            }
        }

        if (updated) {
            saveEvents(events);
            Log.d(TAG, "Event updated: " + event.getName() + ", completed: " + event.isCompleted());
        } else {
            Log.w(TAG, "Event not found for update: " + event.getId());
        }
    }

    public void deleteEvent(String eventId) {
        List<Event> events = loadEvents();
        events.removeIf(event -> event.getId().equals(eventId));
        saveEvents(events);
        Log.d(TAG, "Event deleted: " + eventId);
    }

    public void deleteAllEvents() {
        saveEvents(new ArrayList<>());
        Log.d(TAG, "All events deleted");
    }

    public void deleteEventsFile() {
        File file = getEventsFile();
        if (file.exists()) {
            boolean deleted = file.delete();
            Log.d(TAG, "Events file deleted: " + deleted);
        }
    }

    public List<Event> loadEvents() {
        File file = getEventsFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Event>>() {
            }.getType();
            List<Event> events = gson.fromJson(reader, listType);
            return events != null ? events : new ArrayList<>();
        } catch (IOException e) {
            Log.e(TAG, "Error loading events", e);
            return new ArrayList<>();
        }
    }

    private void saveEvents(List<Event> events) {
        File file = getEventsFile();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(events, writer);
            Log.d(TAG, "Events saved successfully. Total: " + events.size());
        } catch (IOException e) {
            Log.e(TAG, "Error saving events", e);
        }
    }

    public List<Event> getEventsByDate(LocalDate date) {
        List<Event> allEvents = loadEvents();
        List<Event> filteredEvents = new ArrayList<>();

        for (Event event : allEvents) {
            if (event.getRepeatType().equals("daily")) {
                // Daily events show from start date onwards (including start date)
                if (date.equals(event.getDate()) || date.isAfter(event.getDate())) {
                    filteredEvents.add(event);
                }
            } else if (event.getRepeatType().equals("once") && event.getDate().equals(date)) {
                filteredEvents.add(event);
            } else if (event.getRepeatType().equals("selected_days")) {
                // Selected days events show:
                // 1. On the start date (always show on start date regardless of day of week)
                // 2. On selected days from start date onwards
                if (date.equals(event.getDate())) {
                    // Always show on the start date
                    filteredEvents.add(event);
                } else if (date.isAfter(event.getDate())) {
                    // Show on selected days after start date
                    String dayOfWeek = getDayOfWeekInVietnamese(date.getDayOfWeek().getValue());
                    if (event.getSelectedDays() != null && event.getSelectedDays().contains(dayOfWeek)) {
                        filteredEvents.add(event);
                    }
                }
            }
        }

        // Sort events by start time
        Collections.sort(filteredEvents, new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                int time1 = parseTimeToMinutes(e1.getStartTime());
                int time2 = parseTimeToMinutes(e2.getStartTime());
                return Integer.compare(time1, time2);
            }
        });

        return filteredEvents;
    }

    private String getDayOfWeekInVietnamese(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "Thứ Hai";
            case 2:
                return "Thứ Ba";
            case 3:
                return "Thứ Tư";
            case 4:
                return "Thứ Năm";
            case 5:
                return "Thứ Sáu";
            case 6:
                return "Thứ Bảy";
            case 7:
                return "Chủ Nhật";
            default:
                return "";
        }
    }

    public boolean hasTimeConflict(Event newEvent) {
        // For daily events, check conflicts across all days
        if (newEvent.getRepeatType().equals("daily")) {
            return hasTimeConflictForDailyEvent(newEvent);
        }

        // For selected days events, check conflicts on those specific days
        if (newEvent.getRepeatType().equals("selected_days")) {
            return hasTimeConflictForSelectedDaysEvent(newEvent);
        }

        // For one-time events, check conflicts only on that specific date
        List<Event> existingEvents = getEventsByDate(newEvent.getDate());
        return checkTimeConflictWithList(newEvent, existingEvents);
    }

    private boolean hasTimeConflictForDailyEvent(Event newEvent) {
        List<Event> allEvents = loadEvents();

        for (Event existingEvent : allEvents) {
            if (existingEvent.getId().equals(newEvent.getId())) {
                continue;
            }

            // Check if existing event is also daily or overlaps with any day
            if (existingEvent.getRepeatType().equals("daily")) {
                if (hasTimeOverlap(newEvent, existingEvent)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasTimeConflictForSelectedDaysEvent(Event newEvent) {
        if (newEvent.getSelectedDays() == null || newEvent.getSelectedDays().isEmpty()) {
            return false;
        }

        List<Event> allEvents = loadEvents();

        for (Event existingEvent : allEvents) {
            if (existingEvent.getId().equals(newEvent.getId())) {
                continue;
            }

            // Check conflicts based on existing event type
            if (existingEvent.getRepeatType().equals("daily")) {
                // Daily events conflict with any selected day
                if (hasTimeOverlap(newEvent, existingEvent)) {
                    return true;
                }
            } else if (existingEvent.getRepeatType().equals("selected_days")) {
                // Check if any selected days overlap
                if (existingEvent.getSelectedDays() != null) {
                    for (String day : newEvent.getSelectedDays()) {
                        if (existingEvent.getSelectedDays().contains(day) && hasTimeOverlap(newEvent, existingEvent)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean checkTimeConflictWithList(Event newEvent, List<Event> events) {
        for (Event existingEvent : events) {
            if (existingEvent.getId().equals(newEvent.getId())) {
                continue;
            }

            if (hasTimeOverlap(newEvent, existingEvent)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasTimeOverlap(Event event1, Event event2) {
        int start1 = parseTimeToMinutes(event1.getStartTime());
        int end1 = parseTimeToMinutes(event1.getEndTime());
        int start2 = parseTimeToMinutes(event2.getStartTime());
        int end2 = parseTimeToMinutes(event2.getEndTime());

        // Since events must be within the same day, no need to handle overnight events
        // Check overlap: start1 < end2 AND end1 > start2
        return start1 < end2 && end1 > start2;
    }

    private int parseTimeToMinutes(String time) {
        try {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 60 + minutes;
        } catch (Exception e) {
            return 0;
        }
    }

    public Event getConflictingEvent(Event newEvent) {
        List<Event> existingEvents;

        // Get events based on repeat type
        if (newEvent.getRepeatType().equals("daily")) {
            // For daily events, check all events
            existingEvents = loadEvents();
        } else if (newEvent.getRepeatType().equals("selected_days")) {
            // For selected days, need to check events on those days
            existingEvents = new ArrayList<>();
            List<Event> allEvents = loadEvents();

            for (Event event : allEvents) {
                if (event.getRepeatType().equals("daily")) {
                    existingEvents.add(event);
                } else if (event.getRepeatType().equals("selected_days") &&
                        event.getSelectedDays() != null && newEvent.getSelectedDays() != null) {
                    // Check if any days overlap
                    for (String day : newEvent.getSelectedDays()) {
                        if (event.getSelectedDays().contains(day)) {
                            existingEvents.add(event);
                            break;
                        }
                    }
                }
            }
        } else {
            // For once events, only check events on that date
            existingEvents = getEventsByDate(newEvent.getDate());
        }

        int newStartMinutes = parseTimeToMinutes(newEvent.getStartTime());
        int newEndMinutes = parseTimeToMinutes(newEvent.getEndTime());

        for (Event existingEvent : existingEvents) {
            if (existingEvent.getId().equals(newEvent.getId())) {
                continue;
            }

            int existingStartMinutes = parseTimeToMinutes(existingEvent.getStartTime());
            int existingEndMinutes = parseTimeToMinutes(existingEvent.getEndTime());

            // Check overlap
            if (newStartMinutes < existingEndMinutes && newEndMinutes > existingStartMinutes) {
                return existingEvent; // Return the conflicting event
            }
        }

        return null;
    }

    public Event getLastEventOfDay(LocalDate date) {
        List<Event> events = getEventsByDate(date);
        if (events.isEmpty()) {
            return null;
        }

        // Events are already sorted by start time, get the last one by end time
        Event lastEvent = events.get(0);
        int lastEndTime = parseTimeToMinutes(lastEvent.getEndTime());

        for (Event event : events) {
            int endTime = parseTimeToMinutes(event.getEndTime());
            // Handle overnight events
            if (endTime < parseTimeToMinutes(event.getStartTime())) {
                endTime += 24 * 60;
            }
            if (lastEndTime < parseTimeToMinutes(lastEvent.getStartTime())) {
                lastEndTime += 24 * 60;
            }

            if (endTime > lastEndTime) {
                lastEvent = event;
                lastEndTime = endTime;
            }
        }

        return lastEvent;
    }

    public String getSuggestedStartTime(LocalDate date) {
        List<Event> events = getEventsByDate(date);
        if (events.isEmpty()) {
            return "08:00"; // Default start time if no events
        }

        // Find the first available time slot
        int currentTime = 8 * 60; // Start checking from 8:00 AM

        for (Event event : events) {
            int eventStart = parseTimeToMinutes(event.getStartTime());
            int eventEnd = parseTimeToMinutes(event.getEndTime());

            // If there's a gap before this event, suggest this time
            if (currentTime < eventStart) {
                int hour = currentTime / 60;
                int minute = currentTime % 60;
                return String.format("%02d:%02d", hour, minute);
            }

            // Move current time to after this event
            currentTime = Math.max(currentTime, eventEnd);
        }

        // Check if there's still time left in the day
        if (currentTime < 23 * 60 + 59) {
            int hour = currentTime / 60;
            int minute = currentTime % 60;

            // Make sure we don't suggest time past 23:59
            if (hour > 23) {
                return "23:00";
            }

            return String.format("%02d:%02d", hour, minute);
        }

        // If no time slots available, return the earliest event's start time
        return events.get(0).getStartTime();
    }

    public List<TimeSlot> getAvailableTimeSlots(LocalDate date) {
        List<TimeSlot> availableSlots = new ArrayList<>();
        List<Event> events = getEventsByDate(date);

        if (events.isEmpty()) {
            // Whole day is available
            availableSlots.add(new TimeSlot("00:00", "23:59"));
            return availableSlots;
        }

        int dayStart = 0; // 00:00
        int dayEnd = 23 * 60 + 59; // 23:59

        // Check for slot before first event
        int firstEventStart = parseTimeToMinutes(events.get(0).getStartTime());
        if (dayStart < firstEventStart) {
            availableSlots.add(new TimeSlot(
                    formatMinutesToTime(dayStart),
                    formatMinutesToTime(firstEventStart - 1)));
        }

        // Check for slots between events
        for (int i = 0; i < events.size() - 1; i++) {
            int currentEnd = parseTimeToMinutes(events.get(i).getEndTime());
            int nextStart = parseTimeToMinutes(events.get(i + 1).getStartTime());

            if (currentEnd < nextStart) {
                availableSlots.add(new TimeSlot(
                        formatMinutesToTime(currentEnd),
                        formatMinutesToTime(nextStart - 1)));
            }
        }

        // Check for slot after last event
        int lastEventEnd = parseTimeToMinutes(events.get(events.size() - 1).getEndTime());
        if (lastEventEnd < dayEnd) {
            availableSlots.add(new TimeSlot(
                    formatMinutesToTime(lastEventEnd),
                    formatMinutesToTime(dayEnd)));
        }

        return availableSlots;
    }

    private String formatMinutesToTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    public static class TimeSlot {
        public final String startTime;
        public final String endTime;

        public TimeSlot(String startTime, String endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}