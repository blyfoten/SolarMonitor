package se.cfor.solarmonitor;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 */
public class MainFragmentLauncher extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        if (savedInstanceState==null) {

            Fragment frag = new MenuFragment();
            System.out.println("MainFragmentLauncher->onCreate");
            getSupportFragmentManager().beginTransaction().add(R.id.menuFrame, frag).commit();
        }


    }

}
