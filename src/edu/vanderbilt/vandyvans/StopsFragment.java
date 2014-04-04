package edu.vanderbilt.vandyvans;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import edu.vanderbilt.vandyvans.models.Stop;
import edu.vanderbilt.vandyvans.models.Stops;

public final class StopsFragment
        extends    RoboFragment
        implements AdapterView.OnItemClickListener {

    @InjectView(R.id.listView1) private ListView mStopList;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         saved) {
        return inflater.inflate(R.layout.fragment_stop,
                                container,
                                false);
    }
    
    @Override
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);
        
        List<Stop> shortList = new LinkedList<Stop>();
        shortList.addAll(Stops.getShortList());
        shortList.add(Stops.buildSimpleStop(-1, "Other Stops"));

        mStopList.setAdapter(ArrayAdapterBuilder
                .fromCollection(shortList)
                .withContext(getActivity())
                .withResource(R.layout.simple_text)
                .withStringer(new ArrayAdapterBuilder.ToString<Stop>() {
                    public String apply(Stop stop) {
                        return stop.name;
                    }
                })
                .build());
        
        mStopList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapter,
                            View           view,
                            int            position,
                            long           id) {

        Stop selectedStop = (Stop) adapter.getItemAtPosition(position);

        if (selectedStop.id == -1) {
            mStopList.setAdapter(ArrayAdapterBuilder
                    .fromCollection(Stops.getAll())
                    .withContext(getActivity())
                    .withResource(R.layout.simple_text)
                    .withStringer(new ArrayAdapterBuilder.ToString<Stop>() {
                        public String apply(Stop stop) {
                            return stop.name;
                        }
                    })
                    .build());

            mStopList.invalidateViews();
        } else {
            DetailActivity.openForId(
                    selectedStop.id,
                    getActivity());
        }
    }
    
}
