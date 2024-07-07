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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.german_software_engineers.trainerapp.ExerciseView.ViewModel.BodyWeightExerciseViewModel;
import com.german_software_engineers.trainerapp.ExerciseView.ViewModel.ExerciseViewModel;
import com.german_software_engineers.trainerapp.R;
import com.german_software_engineers.trainerappmodel.Exercise.BodyWeightExercise;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BodyWeightExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BodyWeightExerciseFragment extends ExerciseFragment {
    ExerciseViewModel ExercViewModel = null;
    BodyWeightExerciseViewModel BodyWeightExercViewModel = null;

    CheckBox AdditionalInformationCheckbox;
    EditText AdditionalInformationEdit;

    private OnFragmentInteractionListener mListener;

    public BodyWeightExerciseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment BodyWeightExerciseFragment.
     */
    public static BodyWeightExerciseFragment newInstance() {
        BodyWeightExerciseFragment fragment = new BodyWeightExerciseFragment();
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
        return inflater.inflate(R.layout.fragment_body_weight_exercise, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        AdditionalInformationCheckbox = (CheckBox)view.findViewById(R.id.AdditionalInformationCheckbox);
        AdditionalInformationEdit = (EditText)view.findViewById(R.id.AdditionalInformationEditText);

        makeConnections();
        setData();
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

    private void makeConnections(){
        AdditionalInformationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BodyWeightExercViewModel.setAdditionalInformationActivated(AdditionalInformationCheckbox.isChecked());
                AdditionalInformationEdit.setEnabled(AdditionalInformationCheckbox.isChecked());
            }
        });
        AdditionalInformationEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!AdditionalInformationEdit.getText().toString().isEmpty())
                    BodyWeightExercViewModel.setAdditionalInformation(AdditionalInformationEdit.getText().toString());
                else
                    BodyWeightExercViewModel.setAdditionalInformation("");
            }
        });
    }

    private void setData(){
        AdditionalInformationCheckbox.setChecked(BodyWeightExercViewModel.isAdditionalInformationActivated());
        AdditionalInformationEdit.setEnabled(BodyWeightExercViewModel.isAdditionalInformationActivated());
        AdditionalInformationEdit.setText(BodyWeightExercViewModel.getAdditionalInformation());
    }

    public void setExerciseViewModel(ExerciseViewModel model){
        ExercViewModel = model;
        BodyWeightExercViewModel = ExercViewModel.getBodyWeightExerciseViewModel();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
