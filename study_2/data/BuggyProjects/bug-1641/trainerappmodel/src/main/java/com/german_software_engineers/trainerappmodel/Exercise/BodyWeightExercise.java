package com.german_software_engineers.trainerappmodel.Exercise;

import com.german_software_engineers.trainerappmodel.Enumerations.ExerciseType;

public class BodyWeightExercise extends Exercise {
    private ExerciseType Type;

    private boolean IsAdditionalInformationActivated;
    private String AdditionalInformation="";

    public BodyWeightExercise(String name){
        super(name);
        Type=ExerciseType.BodyWeight;
    }

    public ExerciseType type(){
        return Type;
    }

    public String getAdditionalInformation() {
        if(IsAdditionalInformationActivated)
            return AdditionalInformation;
        return "";
    }

    public void setAdditionalInformation(String additionalInformation) {
        if(IsAdditionalInformationActivated)
            AdditionalInformation = additionalInformation;
    }

    public boolean isAdditionalInformationActivated() {
        return IsAdditionalInformationActivated;
    }

    public void setAdditionalInformationActivated(boolean additionalInformationActivated) {
        IsAdditionalInformationActivated = additionalInformationActivated;
    }
}
