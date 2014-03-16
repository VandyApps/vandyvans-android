package edu.vanderbilt.vandyvans.models;

public final class Route {

    public final int               id;
    public final String            name;
    public final Routes.RouteColor color;

    public Route(
            int               _id,
            String            _name,
            Routes.RouteColor _color) {
        id    = _id;
        name  = _name;
        color = _color;
    }

}
