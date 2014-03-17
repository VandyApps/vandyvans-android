package edu.vanderbilt.vandyvans.services;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.vanderbilt.vandyvans.models.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
* Created by athran on 3/16/14.
*/
final class SyncromaticsClient implements Handler.Callback {

    private static final String LOG_TAG = "SyncromaticsClient";
    private static final String BASE_URL = "http://api.syncromatics.com";
    private static final String API_KEY = "?api_key=a922a34dfb5e63ba549adbb259518909";

    private static final JsonParser PARSER = new JsonParser();

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.obj instanceof Global.Initialize)
            return init();

        else if (msg.obj instanceof Global.FetchVans)
            return vans(
                    ((Global.FetchVans) msg.obj).from,
                    ((Global.FetchVans) msg.obj).route);

        else if (msg.obj instanceof Global.FetchArrivalTimes)
            return arrivalTimes(
                    ((Global.FetchArrivalTimes) msg.obj).from,
                    ((Global.FetchArrivalTimes) msg.obj).stop);

        else return false;
    }

    private boolean init() {
        Log.d(LOG_TAG, "Initialization");
        return true;
    }

    private boolean vans(Handler requester, Route route) {
        final StringBuilder buffer = new StringBuilder(BASE_URL)
                .append("/Route/")
                .append(route.id)
                .append("/Vehicles")
                .append(API_KEY);

        try {
            Reader reader = new InputStreamReader(Global.get(buffer.toString()));
            List<Van> result = new LinkedList<Van>();
            for (JsonElement elem : PARSER.parse(reader).getAsJsonArray()) {
                JsonObject obj = elem.getAsJsonObject();
                result.add(new Van(
                        obj.get(Van.TAG_ID).getAsInt(),
                        obj.get(Van.TAG_PERCENT_FULL).getAsInt(),
                        new FloatPair(
                                obj.get(Van.TAG_LATS).getAsDouble(),
                                obj.get(Van.TAG_LOND).getAsDouble())));
            }

            reader.close();
            requester
                    .obtainMessage(0,
                            new Global.VanResults(result))
                    .sendToTarget();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to get Vans for Route.");
            Log.e(LOG_TAG, "URL: " + buffer.toString());
            Log.e(LOG_TAG, e.getMessage());
        }
        return true;
    }

    // http://api.syncromatics.com/Route/745/Stop/263473/Arrivals?api_key=a922a34dfb5e63ba549adbb259518909
    private boolean arrivalTimes(final Handler requester, final Stop stop) {
        //Log.d(LOG_TAG, "Arrival Time request received.");
        List<ArrivalTime> result = new LinkedList<ArrivalTime>();
        for (Route r : Routes.getAll()) {
            ArrivalTime time = readArrivalTimeForRoute(r, stop);
            if (time != null) {
                result.add(time);
            }
        }

        //Log.d(LOG_TAG, "This many Times fetched: " + result.size());

        requester
                .obtainMessage(0,
                        new Global.ArrivalTimeResults(result))
                .sendToTarget();

        return true;
    }

    private ArrivalTime readArrivalTimeForRoute(Route route, final Stop stop) {
        final StringBuilder buffer = new StringBuilder(BASE_URL)
                .append("/Route/")
                .append(route.id)
                .append("/Stop/")
                .append(stop.id)
                .append("/Arrivals")
                .append(API_KEY);

        ArrivalTime result = null;

        try {
            final Reader reader = new InputStreamReader(Global.get(buffer.toString()));
            final JsonObject responseObj = PARSER.parse(reader).getAsJsonObject();
            final JsonObject predictionObj = responseObj
                    .get("Predictions").getAsJsonArray()
                    .get(0).getAsJsonObject();

            result = new ArrivalTime(
                    stop,
                    route,
                    predictionObj.get("Minutes").getAsInt());

            reader.close();

        } catch (Exception e) {
            // This stop may not be in this route.
            // return null
            //Log.e(LOG_TAG, e.getMessage());
            //Log.e(LOG_TAG, buffer.toString());
        }

        return result;
    }
}
