package com.german_software_engineers.trainerapp.ExerciseView.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.german_software_engineers.trainerapp.ExerciseView.ViewModel.ExerciseViewModel;
import com.german_software_engineers.trainerapp.ExerciseView.ViewModel.WarmUpExerciseViewModel;
import com.german_software_engineers.trainerapp.R;
import com.german_software_engineers.trainerappmodel.Enumerations.Intensities;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WarmUpExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WarmUpExerciseFragment extends ExerciseFragment {

    private OnFragmentInteractionListener mListener;

    private ExerciseViewModel ExercViewModel;
    private WarmUpExerciseViewModel WarmUpwExercViewModel;

    private CheckBox TimeCheckbox;
    private EditText TimeEdit;
    private CheckBox IntensotyCheckbox;
    private Spinner IntensitySpinner;
    private CheckBox SubIntensityCheckbox;
    private EditText SubIntensityEdit;
    private CheckBox HeartFrequencyCheckbox;
    private EditText HeartFrequencyEdit;


    public WarmUpExerciseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WarmUpExerciseFragment.
     */
    public static WarmUpExerciseFragment newInstance() {
        WarmUpExerciseFragment fragment = new WarmUpExerciseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_warm_up_exercise, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TimeCheckbox = view.findViewById(R.id.WarmUpTimeCheckBox);
        TimeEdit=view.findViewById(R.id.WarmUpTimeEdit);
        IntensotyCheckbox = view.findViewById(R.id.WarmUpIntensityCheckBox);
        IntensitySpinner = view.findViewById(R.id.WarmUpIntensitySpinner);
        HeartFrequencyCheckbox = view.findViewById(R.id.WarmUpBPMCheckBox);
        HeartFrequencyEdit = view.findViewById(R.id.WarmUpBPMEdit);
        SubIntensityCheckbox = view.findViewById(R.id.WarmUpSubIntensityCheckBox);
        SubIntensityEdit = view.findViewById(R.id.WarmUpSubintensityEdit);

        makeConnections();
        setData();
    }

    private void makeConnections(){
        TimeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WarmUpwExercViewModel.setWarmUpTimeActivated(TimeCheckbox.isChecked());
                TimeEdit.setActivated(TimeCheckbox.isChecked());
            }
        });
        TimeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TimeEdit.getText().toString().isEmpty())
                    WarmUpwExercViewModel.setWamUpTime(Integer.valueOf(TimeEdit.getText().toString()));
                else
                    WarmUpwExercViewModel.setWamUpTime(0);
            }
        });
        HeartFrequencyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WarmUpwExercViewModel.setHeartfrequencyActivated(HeartFrequencyCheckbox.isChecked());
                HeartFrequencyEdit.setActivated(HeartFrequencyCheckbox.isChecked());
            }
        });
        HeartFrequencyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!HeartFrequencyEdit.getText().toString().isEmpty())
                    WarmUpwExercViewModel.setHeartfrequency(Integer.valueOf(HeartFrequencyEdit.getText().toString()));
                else
                    WarmUpwExercViewModel.setHeartfrequency(0);
            }
        });
        SubIntensityCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WarmUpwExercViewModel.setSubIntensityActivated(SubIntensityCheckbox.isChecked());
                SubIntensityEdit.setActivated(SubIntensityCheckbox.isChecked());
            }
        });
        SubIntensityEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!SubIntensityEdit.getText().toString().isEmpty())
                    WarmUpwExercViewModel.setSubIntensity(Integer.valueOf(SubIntensityEdit.getText().toString()));
                else
                    WarmUpwExercViewModel.setSubIntensity(0);
            }
        });
        IntensotyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WarmUpwExercViewModel.setIntensityActivated(IntensotyCheckbox.isChecked());
                SubIntensityEdit.setActivated(IntensotyCheckbox.isChecked());
            }
        });
        IntensitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                WarmUpwExercViewModel.setIntensity(Intensities.values()[(int)IntensitySpinner.getSelectedItemId()]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void setData(){
        TimeCheckbox.setChecked(WarmUpwExercViewModel.isWarmUpTimeActivated());
        TimeEdit.setText(String.valueOf(WarmUpwExercViewModel.getWamUpTime()));
        IntensotyCheckbox.setChecked(WarmUpwExercViewModel.isIntensityActivated());
        if(WarmUpwExercViewModel.getIntensity()!= Intensities.invalid)
            IntensitySpinner.setSelection(WarmUpwExercViewModel.getIntensity().ordinal());
        SubIntensityCheckbox.setChecked(WarmUpwExercViewModel.isSubIntensityActivated());
        SubIntensityEdit.setText(String.valueOf(WarmUpwExercViewModel.getSubIntensity()));
        HeartFrequencyCheckbox.setChecked(WarmUpwExercViewModel.isHeartfrequencyActivated());
        HeartFrequencyEdit.setText(String.valueOf(WarmUpwExercViewModel.getHeartfrequency()));
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

    public void setExerciseViewModel(ExerciseViewModel viewModel){
        ExercViewModel = viewModel;
        WarmUpwExercViewModel = ExercViewModel.getWarmUpExerciseViewModel();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
