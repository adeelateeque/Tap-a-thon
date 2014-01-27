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

import java.util.UUID;

public class CreateChannelFragment extends DialogFragment {

    private LinearLayout view;
    private EditText etChannelName;
    private RadioGroup rgLvl;
    private Button btnCreate, btnCancel;
    private PadView.GameLevel selection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Create a Tapathon");
        view = (LinearLayout) inflater.inflate(R.layout.dialog_create_channel, null);
        etChannelName = (EditText) view.findViewById(R.id.et_name);
        etChannelName.setEnabled(false);
        etChannelName.setText((getString(R.string.room).concat(UUID.randomUUID().toString().substring(0, 4)).toUpperCase()));
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
                        selection = PadView.GameLevel.EASY;
                        break;

                    case R.id.rb_normal:
                        selection = PadView.GameLevel.MEDIUM;
                        break;

                    case R.id.rb_hard:
                        selection = PadView.GameLevel.HARD;
                        break;
                }

                if ((selection != null)) {
                    Intent intent = new Intent(getActivity(), GamePadActivity.class);
                    intent.putExtra("level", selection);
                    intent.putExtra(GamePadActivity.CLIENT, false);
                    intent.putExtra(GamePadActivity.SERVER_NAME, etChannelName.getText().toString());
                    startActivity(intent);
                    getDialog().dismiss();
                    getActivity().finish();
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
