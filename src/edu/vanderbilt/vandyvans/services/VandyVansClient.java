package edu.vanderbilt.vandyvans.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.JsonWriter;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.vanderbilt.vandyvans.models.FloatPair;
import edu.vanderbilt.vandyvans.models.Report;
import edu.vanderbilt.vandyvans.models.Route;
import edu.vanderbilt.vandyvans.models.Stop;

import static edu.vanderbilt.vandyvans.services.Global.APP_LOG_ID;

/**
* Created by athran on 3/16/14.
*/
final class VandyVansClient implements Handler.Callback {

    private static final String     LOG_TAG    = "VandyVansClient";
    private static final String     BASE_URL   = "http://vandyvans.com";
    private static final String     REPORT_URL = "http://studentorgs.vanderbilt.edu/vandymobile/bugReport.php";
    private static final JsonParser PARSER     = new JsonParser();

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

        else if (msg.obj instanceof Report)
            return true; //postReport((Report) msg.obj); TODO

        else return false;
    }

    private boolean init() {
        Log.d(APP_LOG_ID, LOG_TAG + " | Initialization");
        return true;
    }

    private boolean fetchStops(Handler requester, Route r) {

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
            requester
                    .obtainMessage(0, new Global.StopResults(result))
                    .sendToTarget();

        } catch (Exception e) {
            Log.e(APP_LOG_ID, LOG_TAG + " | Failed to get Stops for Route.");
            Log.e(APP_LOG_ID, LOG_TAG + " | URL: " + buffer.toString());
            Log.e(APP_LOG_ID, e .getMessage());
        }
        return true;
    }

    private boolean waypoints(Handler requester, Route r) {

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

            requester
                    .obtainMessage(0,
                            new Global.WaypointResults(result))
                    .sendToTarget();

        } catch (Exception e) {
            Log.e(APP_LOG_ID, LOG_TAG + " | Failed to get Waypoints for Route.");
            Log.e(APP_LOG_ID, LOG_TAG + " | URL: " + buffer.toString());
            Log.e(APP_LOG_ID, e.getMessage());
        }
        return true;
    }

    private boolean postReport(Report report) {

        try {

            //final Map<String,String> params = generateKeyValuedOutput(report);
            final String jsonOutput = generateJsonOutput(report);

            final BufferedReader respReader = new BufferedReader(
                    new InputStreamReader(
                            //Global.postUrlRequest(REPORT_URL, params)
                            Global.post(REPORT_URL, jsonOutput)
                    ));

            Log.i(APP_LOG_ID, LOG_TAG + " | Vandy Vans server response for report.");
            //Log.i(LOG_TAG, buffer.toString());
            for (String line = respReader.readLine(); // Yeah motherfucker
                 line != null;
                 line = respReader.readLine()) {
                Log.i(APP_LOG_ID, line);
            }

        } catch (Exception e) {
            Log.e(APP_LOG_ID, LOG_TAG + " | Failed to send report");
            Log.e(APP_LOG_ID, report.toString());
            //Log.e(LOG_TAG, buffer.toString());
            Log.e(APP_LOG_ID, e.getMessage());
        }

        return true;
    }

    private String generateJsonOutput(Report report) {
        final StringWriter buffer = new StringWriter();
        final JsonWriter writer = new JsonWriter(buffer);

        try {
            writer.beginObject();
            writer.name("verifyHash")
                    .value(Global.encryptPassword("vandyvansapp"));
            writer.name("isBugReport")
                    .value(report.isBugReport? "TRUE" : "FALSE");
            writer.name("senderAddress")
                    .value(report.senderAddress);
            writer.name("body")
                    .value(report.bodyOfReport);
            writer.name("notifyWhenResolved")
                    .value(report.notifyWhenResolved);
            writer.endObject();
        } catch (IOException e) {
            return "";
        }

        return buffer.toString();
    }

    private Map<String,String> generateKeyValuedOutput(Report report) {
        final Map<String,String> params = new HashMap<String,String>();
        params.put("verifyHash"        , Global.encryptPassword("vandyvansapp"));
        params.put("isBugReport"       , report.isBugReport? "TRUE" : "FALSE");
        params.put("senderAddress"     , report.senderAddress);
        params.put("body"              , report.bodyOfReport);
        params.put("notifyWhenResolved", report.notifyWhenResolved? "TRUE" : "FALSE");
        return params;
    }

}
