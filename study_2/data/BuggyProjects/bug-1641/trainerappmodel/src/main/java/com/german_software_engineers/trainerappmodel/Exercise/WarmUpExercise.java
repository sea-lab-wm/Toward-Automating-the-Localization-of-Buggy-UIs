package com.german_software_engineers.trainerappmodel.Exercise;

import com.german_software_engineers.trainerappmodel.Enumerations.ExerciseType;
import com.german_software_engineers.trainerappmodel.Enumerations.Intensities;

public class WarmUpExercise extends Exercise {
    private ExerciseType Type;
    private boolean IsExecutionTimeActivated = false;
    private int ExecutionTime=Integer.MAX_VALUE;
    private boolean IsIntensityActivated = false;
    private Intensities Intenity=Intensities.invalid;
    private boolean IsSubintensityActivated;
    private int SubIntensity=Integer.MAX_VALUE;
    private boolean IsBPMActivated = false;
    private int BPM=Integer.MAX_VALUE;

    public WarmUpExercise(String name){
        super(name);
        Type = ExerciseType.WarmUp;
    }

    public ExerciseType type(){
        return Type;
    }


    public int getExecutionTime() {
        if(IsExecutionTimeActivated)
            return ExecutionTime;
        return Integer.MAX_VALUE;
    }

    public void setExecutionTime(int executionTime) {
        if(IsExecutionTimeActivated)
            ExecutionTime = executionTime;
    }

    public Intensities getIntenity() {
        if(IsIntensityActivated)
            return Intenity;
        return Intensities.invalid;
    }

    public void setIntenity(Intensities intenity) {
        if(IsIntensityActivated)
            Intenity = intenity;
    }

    public void setSubIntensity(int subIntensity){
        if(IsSubintensityActivated)
            SubIntensity=subIntensity;
    }

    public int getSubIntensity() {
        if(IsSubintensityActivated)
            return SubIntensity;
        return Integer.MAX_VALUE;
    }

    public int getBPM() {
        if(IsBPMActivated)
            return BPM;
        return Integer.MAX_VALUE;
    }

    public void setBPM(int BPM) {
        if(IsBPMActivated)
            this.BPM = BPM;
    }

    public boolean isExecutionTimeActivated() {
        return IsExecutionTimeActivated;
    }

    public void setExecutionTimeActivated(boolean executionTimeActivated) {
        IsExecutionTimeActivated = executionTimeActivated;
    }

    public boolean isIntensityActivated() {
        return IsIntensityActivated;
    }

    public void setIntensityActivated(boolean intensityActivated) {
        IsIntensityActivated = intensityActivated;
    }

    public boolean isSubintensityActivated() {
        return IsSubintensityActivated;
    }

    public void setSubintensityActivated(boolean subintensityActivated) {
        IsSubintensityActivated = subintensityActivated;
    }

    public boolean isBPMActivated() {
        return IsBPMActivated;
    }

    public void setBPMActivated(boolean BPMActivated) {
        IsBPMActivated = BPMActivated;
    }
}
