package com.german_software_engineers.trainerapp.ExerciseView.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.german_software_engineers.trainerapp.ExerciseView.ViewModel.DeviceExerciseViewModel;
import com.german_software_engineers.trainerapp.ExerciseView.ViewModel.ExerciseViewModel;
import com.german_software_engineers.trainerapp.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceExerciseFragment extends ExerciseFragment {
    ExerciseViewModel ExercViewModel = null;
    DeviceExerciseViewModel DeviceExercViewModel = null;

    private OnFragmentInteractionListener mListener;

    private CheckBox SeatCheckbox;
    private EditText SeatEdit;
    private CheckBox DeviceCheckbox;
    private EditText DeviceEdit;
    private CheckBox LegCheckbox;
    private EditText LegEdit;
    private CheckBox FootCheckbox;
    private EditText FootEdit;
    private CheckBox AngleCheckbox;
    private EditText AngleEdit;
    private CheckBox BackCheckbox;
    private EditText BackEdit;
    private CheckBox WeightCheckbox;
    private EditText WeightEdit;
    private CheckBox AdditionalWeightCheckbox;
    private EditText AdditionalWeightEdit;



    public DeviceExerciseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeviceExerciseFragment.
     */
    public static DeviceExerciseFragment newInstance() {
        DeviceExerciseFragment fragment = new DeviceExerciseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            Exercise = getArguments().getClass(ARG_EXERCISE);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_exercise, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SeatCheckbox = (CheckBox)view.findViewById(R.id.seatCheckBox2);
        SeatEdit = (EditText)view.findViewById(R.id.SeatEdit2);
        DeviceCheckbox = (CheckBox)view.findViewById(R.id.deviceCheckBox2);
        DeviceEdit = (EditText)view.findViewById(R.id.DevicetEdit2);
        LegCheckbox = (CheckBox)view.findViewById(R.id.LegCheckBox2);
        LegEdit = (EditText)view.findViewById(R.id.LegEdit2);
        FootCheckbox=(CheckBox)view.findViewById(R.id.FootCheckBox2);
        FootEdit = (EditText)view.findViewById(R.id.FootEdit2);
        AngleCheckbox=(CheckBox)view.findViewById(R.id.AngleCheckBox2);
        AngleEdit = (EditText)view.findViewById(R.id.AngleEdit2);
        BackCheckbox=(CheckBox)view.findViewById(R.id.BackCheckBox2);
        BackEdit = (EditText)view.findViewById(R.id.BackEdit2);
        WeightCheckbox=(CheckBox)view.findViewById(R.id.WeightCheckBox2);
        WeightEdit = (EditText)view.findViewById(R.id.WeightEdit2);
        AdditionalWeightCheckbox=(CheckBox)view.findViewById(R.id.AdditionalWeightCheckBox2);
        AdditionalWeightEdit = (EditText)view.findViewById(R.id.AdditionalWeightEdit2);


        makeConnections();
        setData();
    }

    private void makeConnections(){
        SeatCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeviceExercViewModel.setSeatActivated(SeatCheckbox.isChecked());
                SeatEdit.setActivated(DeviceExercViewModel.isSeatActivated());
            }
        });
        SeatEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!SeatEdit.getText().toString().isEmpty())
                    DeviceExercViewModel.setSeatPosition(Integer.valueOf(SeatEdit.getText().toString()));
                else
                    DeviceExercViewModel.setSeatPosition(0);
            }
        });
        DeviceCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeviceExercViewModel.setDeviceActivated(DeviceCheckbox.isChecked());
                DeviceEdit.setActivated(DeviceExercViewModel.isDeviceActivated());
            }
        });
        DeviceEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!DeviceEdit.getText().toString().isEmpty())
                    DeviceExercViewModel.setDevicePosition(Integer.valueOf(DeviceEdit.getText().toString()));
                else
                    DeviceExercViewModel.setDevicePosition(0);
            }
        });
        FootCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeviceExercViewModel.setFootActivated(FootCheckbox.isChecked());
                DeviceEdit.setActivated(DeviceExercViewModel.isFootActivated());
            }
        });
        FootEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!FootEdit.getText().toString().isEmpty())
                    DeviceExercViewModel.setFootPosition(Integer.valueOf(FootEdit.getText().toString()));
                else
                    DeviceExercViewModel.setFootPosition(0);
            }
        });
        LegCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeviceExercViewModel.setLegActivated(LegCheckbox.isChecked());
                LegEdit.setActivated(DeviceExercViewModel.isLegActivated());
            }
        });
        LegEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!LegEdit.getText().toString().isEmpty())
                    DeviceExercViewModel.setLegPosition(Integer.valueOf(LegEdit.getText().toString()));
                else
                    DeviceExercViewModel.setLegPosition(0);
            }
        });
        AngleCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeviceExercViewModel.setAngleActivated(AngleCheckbox.isChecked());
                AngleEdit.setActivated(DeviceExercViewModel.isAngleActivated());
            }
        });
        AngleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!AngleEdit.getText().toString().isEmpty())
                    DeviceExercViewModel.setAnglePosition(Integer.valueOf(AngleEdit.getText().toString()));
                else
                    DeviceExercViewModel.setAnglePosition(0);
            }
        });
        BackCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeviceExercViewModel.setBackActivated(BackCheckbox.isChecked());
                BackEdit.setActivated(DeviceExercViewModel.isBackActivated());
            }
        });
        BackEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!BackEdit.getText().toString().isEmpty())
                    DeviceExercViewModel.setBackPosition(Integer.valueOf(BackEdit.getText().toString()));
                else
                    DeviceExercViewModel.setBackPosition(0);
            }
        });
        WeightCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeviceExercViewModel.setWeightActivated(WeightCheckbox.isChecked());
                WeightEdit.setActivated(DeviceExercViewModel.isWeightActivated());
            }
        });
        WeightEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!WeightEdit.getText().toString().isEmpty())
                    DeviceExercViewModel.setWeight(Double.valueOf(WeightEdit.getText().toString()));
                else
                    DeviceExercViewModel.setWeight(0.0);
            }
        });
        AdditionalWeightCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeviceExercViewModel.setAdditionalWeightActivated(AdditionalWeightCheckbox.isChecked());
                AdditionalWeightEdit.setActivated(DeviceExercViewModel.isAdditionalWeightActivated());
            }
        });
        AdditionalWeightEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!AdditionalWeightEdit.getText().toString().isEmpty())
                    DeviceExercViewModel.setAdditionalWeight(Double.valueOf(AdditionalWeightEdit.getText().toString()));
                else
                    DeviceExercViewModel.setAdditionalWeight(0.0);
            }
        });
    }

    public void setExerciseViewModel(ExerciseViewModel model){
        ExercViewModel=model;
        DeviceExercViewModel=ExercViewModel.getDeviceExerciseViewModel();
//        setData();
    }

    private void setData(){
        SeatCheckbox.setChecked(DeviceExercViewModel.isSeatActivated());
        SeatEdit.setActivated(DeviceExercViewModel.isSeatActivated());
        SeatEdit.setText(String.valueOf(DeviceExercViewModel.getSeatPosition()));
        LegCheckbox.setChecked(DeviceExercViewModel.isLegActivated());
        LegEdit.setActivated(DeviceExercViewModel.isLegActivated());
        LegEdit.setText(String.valueOf(DeviceExercViewModel.getLegPosition()));
        DeviceCheckbox.setChecked(DeviceExercViewModel.isDeviceActivated());
        DeviceEdit.setActivated(DeviceExercViewModel.isDeviceActivated());
        DeviceEdit.setText(String.valueOf(DeviceExercViewModel.getDevicePosition()));
        FootCheckbox.setChecked(DeviceExercViewModel.isFootActivated());
        FootEdit.setActivated(DeviceExercViewModel.isFootActivated());
        FootEdit.setText(String.valueOf(DeviceExercViewModel.getFootPosition()));
        AngleCheckbox.setChecked(DeviceExercViewModel.isAngleActivated());
        AngleEdit.setActivated(DeviceExercViewModel.isAngleActivated());
        AngleEdit.setText(String.valueOf(DeviceExercViewModel.getAnglePosition()));
        BackCheckbox.setChecked(DeviceExercViewModel.isBackActivated());
        BackEdit.setActivated(DeviceExercViewModel.isBackActivated());
        BackEdit.setText(String.valueOf(DeviceExercViewModel.getBackPosition()));
        WeightCheckbox.setChecked(DeviceExercViewModel.isWeightActivated());
        WeightEdit.setActivated(DeviceExercViewModel.isWeightActivated());
        WeightEdit.setText(String.valueOf(DeviceExercViewModel.getWeight()));
        AdditionalWeightCheckbox.setChecked(DeviceExercViewModel.isAdditionalWeightActivated());
        AdditionalWeightEdit.setActivated(DeviceExercViewModel.isAdditionalWeightActivated());
        AdditionalWeightEdit.setText(String.valueOf(DeviceExercViewModel.getAdditionalWeight()));
    }
}
