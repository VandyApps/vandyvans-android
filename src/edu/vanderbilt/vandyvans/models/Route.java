package edu.vanderbilt.vandyvans.models;

import edu.vanderbilt.vandyvans.models.Routes.RouteColor;

public final class Route {

    public final int        id;
    public final String     name;
    public final RouteColor color;

    public Route(
            int        _id,
            String     _name,
            RouteColor _color) {
        id    = _id;
        name  = _name;
        color = _color;
    }

}
