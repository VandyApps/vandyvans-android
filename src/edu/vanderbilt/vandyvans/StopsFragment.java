package edu.vanderbilt.vandyvans;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public final class StopsFragment extends Fragment implements OnItemClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        return inflater.inflate(R.layout.fragment_stop, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);
        
        List<Stop> tmp2 = new LinkedList<Stop>();
        tmp2.addAll(Stops.getShortList());
        tmp2.add(Stops.buildSimpleStop(-1, "Other Stops"));
        
        ListView v = (ListView) getView().findViewById(R.id.listView1);
        
        v.setAdapter(ArrayAdapterBuilder
                .fromCollection(tmp2)
                .withContext(getActivity())
                .withResource(R.layout.simple_text)
                .withStringer(new ArrayAdapterBuilder.ToString<Stop>() {
                    public String apply(Stop stop) {
                        return stop.name;
                    }
                })
                .build());
        
        v.setOnItemClickListener(this);
    }
    

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        DetailActivity.open(getActivity());
        
    }
    
}
