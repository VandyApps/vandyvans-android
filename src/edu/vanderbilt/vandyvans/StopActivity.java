package edu.vanderbilt.vandyvans;

import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.inject.Inject;

import edu.vanderbilt.vandyvans.services.Global;
import edu.vanderbilt.vandyvans.services.VandyClients;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

public final class StopActivity extends RoboFragmentActivity
        implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    final Fragment     mStopFragment = new StopsFragment();
    SupportMapFragment mMapFrag;

    MapController mapController;

    @InjectView(R.id.linear1)   LinearLayout mBar;
    @InjectView(R.id.btn_blue)  Button       mBlueButton;
    @InjectView(R.id.btn_red)   Button       mRedButton;
    @InjectView(R.id.btn_green) Button       mGreenButton;

    @Inject VandyClients clients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Initialization of the map with proper settings
        mMapFrag = SupportMapFragment.newInstance(
                new GoogleMapOptions()
                        .zoomControlsEnabled(false)
                        .camera(CameraPosition.fromLatLngZoom(
                                new LatLng(Global.DEFAULT_LATITUDE,
                                           Global.DEFAULT_LONGITUDE),
                                MapController.DEFAULT_ZOOM
                        )));

        mapController = new MapController(mMapFrag,
                                          mBar,
                                          mBlueButton,
                                          mRedButton,
                                          mGreenButton,
                                          clients);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            AboutsActivity.open(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && getActionBar().getSelectedNavigationIndex() == 1) {
            getActionBar().setSelectedNavigationItem(0);
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }
    
    @Override
    public void onTabSelected(final ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        if (tab.getPosition() == 0) {
            mapController.hideOverlay();
        } else if (tab.getPosition() == 1) {
            mapController.showOverlay();
            mapController.mapIsShown();
        }

        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch(position) {
            case 0:
                return mStopFragment;
            case 1:
                return mMapFrag;
            default:
                return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
            case 0:
                return getString(R.string.stops_label).toUpperCase(l);
            case 1:
                return getString(R.string.map_label).toUpperCase(l);
            default:
                return "";
            }
        }
    }

}
