package edu.vanderbilt.vandyvans;

import java.util.Arrays;
import java.util.List;

/**
 * Created by athran on 3/15/14.
 */
public final class Stops {

    public static Stop getForId(int id) {
        for (Stop s: stoplist) {
            if (s.id == id) return s;
        }
        throw new IllegalArgumentException("No Stop with that Id exist");
    }

    public static Stop buildSimpleStop(int id, String name) {
        return new Stop(id, name, "", 0, 0, 0);
    }

    public static List<Stop> getShortList() {
        return stoplist.subList(0, 4);
    }

    private static List<Stop> stoplist = Arrays.asList(
            buildSimpleStop(263473, "Branscomb Quad"),
            buildSimpleStop(263470, "Carmichael Tower"),
            buildSimpleStop(263454, "Murray House"),
            buildSimpleStop(263444, "Highland Quad"),

            buildSimpleStop(264041, "Vanderbilt Police Department"),
            buildSimpleStop(332298, "Vanderbilt Book Store"),
            buildSimpleStop(263415, "Kissam Quad"),
            buildSimpleStop(238083, "Terrace Place Garage"),
            buildSimpleStop(238096, "Wesley Place Garage"),
            buildSimpleStop(263463, "North House"),
            buildSimpleStop(264091, "Blair School of Music"),
            buildSimpleStop(264101, "McGugin Center"),
            buildSimpleStop(401204, "Blakemore House"),
            buildSimpleStop(446923, "Medical Center")
    );



}
