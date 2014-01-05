package com.zhideel.tapathon.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.zhideel.tapathon.R;

public class CreateChannelFragment extends DialogFragment {

    private LinearLayout view;
    private EditText etChannelName;
    private CheckBox cbAllShare;
    private RadioGroup rgLvl;
    private Button btnCreate, btnCancel;
    private MultiTouchView.GameLevel selection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Select difficulty level:");
        view = (LinearLayout) inflater.inflate(R.layout.dialog_create_channel, null);
        /*etChannelName = (EditText) view.findViewById(R.id.et_name);
        cbAllShare = (CheckBox) view.findViewById(R.id.cb_allshare);*/
        rgLvl = (RadioGroup) view.findViewById(R.id.rg_lvl);
        btnCreate = (Button) view.findViewById(R.id.btn_create);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCreate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (rgLvl.getCheckedRadioButtonId()) {
                    case R.id.rb_easy:
                        selection = MultiTouchView.GameLevel.EASY;
                        break;

                    case R.id.rb_normal:
                        selection = MultiTouchView.GameLevel.MEDIUM;
                        break;

                    case R.id.rb_hard:
                        selection = MultiTouchView.GameLevel.HARD;
                        break;
                }

                if ((selection != null)) {
                    //make connection
                    //Toast.makeText(getActivity(), selection.toString(), Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(CreateChannelFragment.this.getActivity(), GamePadActivity.class);
                    myIntent.putExtra("level", selection);
                    startActivity(myIntent);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please select a difficulty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }
}
