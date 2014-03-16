package edu.vanderbilt.vandyvans.services;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.vanderbilt.vandyvans.models.FloatPair;
import edu.vanderbilt.vandyvans.models.Route;
import edu.vanderbilt.vandyvans.models.Stop;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
* Created by athran on 3/16/14.
*/
final class VandyVansClient implements Handler.Callback {

    private static final String LOG_TAG = "VandyVansClient";
    private static final String BASE_URL = "http://vandyvans.com";
    private static final JsonParser PARSER = new JsonParser();

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.obj instanceof Global.Initialize)
            return init();

        else if (msg.obj instanceof Global.FetchStops)
            return fetchStops(
                    ((Global.FetchStops) msg.obj).from,
                    ((Global.FetchStops) msg.obj).route);

        else if (msg.obj instanceof Global.FetchWaypoints)
            return waypoints(
                    ((Global.FetchWaypoints) msg.obj).from,
                    ((Global.FetchWaypoints) msg.obj).route);

        else return false;
    }

    private boolean init() {
        Log.d(LOG_TAG, "Initialization");
        return true;
    }

    private boolean fetchStops(Handler from, Route r) {

        StringBuilder buffer = new StringBuilder(BASE_URL)
                .append("/Route/")
                .append(r.id)
                .append("/Direction/0/Stops");

        try {
            Reader reader = new InputStreamReader(Global.get(buffer.toString()));
            List<Stop> result = new LinkedList<Stop>();
            for (JsonElement elem : PARSER.parse(reader).getAsJsonArray()) {
                JsonObject obj = elem.getAsJsonObject();
                result.add(new Stop(
                        obj.get(Stop.TAG_ID).getAsInt(),
                        obj.get(Stop.TAG_NAME).getAsString(),
                        obj.get(Stop.TAG_IMAGE).getAsString(),
                        obj.get(Stop.TAG_LAT).getAsDouble(),
                        obj.get(Stop.TAG_LON).getAsDouble(),
                        obj.get(Stop.TAG_RTPI).getAsInt()));
            }

            reader.close();
            from.sendMessage(from.obtainMessage(0, new Global.StopResults(result)));

        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to get Stops for Route.");
            Log.e(LOG_TAG, "URL: " + buffer.toString());
            Log.e(LOG_TAG, e.getMessage());
        }
        return true;
    }

    private boolean waypoints(Handler from, Route r) {

        StringBuilder buffer = new StringBuilder(BASE_URL)
                .append("/Route/")
                .append(r.id)
                .append("/Waypoints");

        try {
            Reader reader = new InputStreamReader(Global.get(buffer.toString()));
            List<FloatPair> result = new LinkedList<FloatPair>();
            for (JsonElement elem : PARSER.parse(reader).getAsJsonArray()) {
                JsonObject obj = elem.getAsJsonObject();
                result.add(new FloatPair(
                        obj.get(FloatPair.TAG_LAT).getAsDouble(),
                        obj.get(FloatPair.TAG_LON).getAsDouble()));
            }

            reader.close();
            from.sendMessage(from.obtainMessage(0, new Global.Waypoints(result)));

        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to get Waypoints for Route.");
            Log.e(LOG_TAG, "URL: " + buffer.toString());
            Log.e(LOG_TAG, e.getMessage());
        }
        return true;
    }
}
