package se.cfor.solarmonitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by FORSLUNC on 2015-03-22.
 */
public class ConsoleFragment extends Fragment implements DataListener {
    private String command;
    private View view;
    private EditText commandEditText;
    private TextView consoleOutTextView;
    private ScrollView scroll;

    public ConsoleFragment() {
    }

    @Override
    public void onStart() {
        ((MyApplication)getActivity().getApplication()).addDataListener(this);
        System.out.println("ConsoleFragment->onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        ((MyApplication)getActivity().getApplication()).removeDataListener(this);
        System.out.println("ConsoleFragment->onStop");
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("ConsoleFragment->onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("ConsoleFragment->onResume");
        // todo: show softkeyboard
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content,container,false);
        System.out.println("ConsoleFragment->onCreate");

        consoleOutTextView = (TextView)view.findViewById(R.id.consoleTextView);
        scroll = (ScrollView)view.findViewById(R.id.scrollView);
        commandEditText = (EditText)view.findViewById(R.id.editText);
        commandEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        commandEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if  ( ( event != null) &&
                      ( event.getAction() == KeyEvent.ACTION_DOWN ) &&
                      ( event.getKeyCode() ==  KeyEvent.KEYCODE_ENTER  ) )
                {
                    command = v.getText().toString() + "\r";
                    v.setText("");
                    System.out.println(command);
                    ((MyApplication)(getActivity().getApplication())).writeToSocket(command);
                    return true;
                }
                else if (( event != null))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });


        return view;
    }



    @Override
    public void onNewData(String data) {
        final String text = data;
        if ( (getActivity() != null) &&
             (consoleOutTextView!= null) &&
             ( data != null ))
        {
            getActivity().runOnUiThread((new Runnable() {
                @Override
                public void run() {
                    consoleOutTextView.append(text);
                    if (scroll!=null) {
                        scroll.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                }
            }));


        }

    }
}
