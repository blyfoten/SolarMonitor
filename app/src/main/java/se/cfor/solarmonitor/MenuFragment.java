package se.cfor.solarmonitor;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

/**
 * Created by FORSLUNC on 2015-03-22.
 */
public class MenuFragment extends Fragment {
    BluetoothSocket btSocket;
    Fragment frag;
    FragmentTransaction fragmentTransaction;
    public MenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu, container, false);
        System.out.println("MenuFragment->onCreateView");
        if (savedInstanceState == null) {
            frag = new ConsoleFragment();
            fragmentTransaction = getFragmentManager().beginTransaction().add(R.id.contentFrame, frag);
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.commit();
        }

        Button btnConsole = (Button)view.findViewById(R.id.buttonOpenConsole);
        btnConsole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = new ConsoleFragment();
                fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.contentFrame, frag);
                fragmentTransaction.disallowAddToBackStack();
                fragmentTransaction.commit();
            }
        });

        Button btnPlot = (Button)view.findViewById(R.id.buttonOpenPlot);
        btnPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = new PlotFragment();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.contentFrame, frag);
                fragmentTransaction.disallowAddToBackStack();
                fragmentTransaction.commit();

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("MenuFragment->onDestroyView");
    }
}
