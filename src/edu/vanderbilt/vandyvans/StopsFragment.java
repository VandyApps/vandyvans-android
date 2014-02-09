package edu.vanderbilt.vandyvans;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

final public class StopsFragment extends Fragment implements OnItemClickListener {

    static List<Stop> stops = Arrays.asList(
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
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        return inflater.inflate(R.layout.fragment_stop, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);
        
        List<Stop> tmp2 = new LinkedList<Stop>();
        tmp2.addAll(stops.subList(0, 4));
        tmp2.add(buildSimpleStop(-1, "Other Stops"));
        
        ListView v = (ListView) getView().findViewById(R.id.listView1);
        v.setAdapter(new ArrayAdapter<Stop>(
                getActivity(),
                R.layout.simple_text,
                tmp2));
        v.setOnItemClickListener(this);
    }
    
    static Stop buildSimpleStop(int id, String name) {
        return new Stop(id, name, "", 0, 0, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        DetailActivity.open(getActivity());
        
    }
    
}
