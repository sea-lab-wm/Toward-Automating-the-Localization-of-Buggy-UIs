package com.german_software_engineers.trainerappmodel.Exercise;

import com.german_software_engineers.trainerappmodel.Enumerations.ExerciseType;

public class DeviceExercise extends Exercise {
    private boolean IsSeatActivated;
    private int SeatPosition=Integer.MAX_VALUE;
    private boolean IsLegActivated;
    private int LegPosition=Integer.MAX_VALUE;
    private boolean IsFootActivated;
    private int FootPosition=Integer.MAX_VALUE;
    private boolean IsAngleActivated;
    private int AnglePosition=Integer.MAX_VALUE;
    private boolean IsWeightActivated;
    private double Weight=Double.MAX_VALUE;
    private boolean IsAdditionalWeightActivated;
    private double AdditionalWeight=Double.MAX_VALUE;
    private boolean IsBackActivated;
    private int BackPosition=Integer.MAX_VALUE;
    private boolean IsDeviceNumberActivated;
    private int DeviceNumber=Integer.MAX_VALUE;
    private ExerciseType Type;

    public DeviceExercise(String name){
        super(name);
        Type=ExerciseType.Device;
    }

    public void setSeatPosition(int pos){
        if(IsSeatActivated)
            SeatPosition = pos;
    }


    public int getSeatPosition() {
        if(IsSeatActivated)
            return SeatPosition;
        else
            return Integer.MAX_VALUE;
    }

    public int getLegPosition() {
        if(IsLegActivated)
            return LegPosition;
        else
            return Integer.MAX_VALUE;
    }

    public void setLegPosition(int legPosition) {
        if(IsLegActivated)
            LegPosition = legPosition;
    }

    public int getFootPosition() {
        if(IsFootActivated)
            return FootPosition;
        else
            return Integer.MAX_VALUE;
    }

    public void setFootPosition(int footPosition) {
        if(IsFootActivated)
            FootPosition = footPosition;
    }

    public int getAnglePosition() {
        if(IsAngleActivated)
            return AnglePosition;
        else
            return Integer.MAX_VALUE;
    }

    public void setAnglePosition(int anglePosition) {
        if(IsAngleActivated)
            AnglePosition = anglePosition;
    }

    public double getWeight() {
        if(IsWeightActivated)
            return Weight;
        else
            return Double.MAX_VALUE;
    }

    public void setWeight(double weight) {
        if(IsWeightActivated)
            Weight = weight;
    }



    public int getBackPosition() {
        if(IsBackActivated)
            return BackPosition;
        else
            return Integer.MAX_VALUE;
    }

    public void setBackPosition(int backPosition) {
        if(IsBackActivated)
            BackPosition = backPosition;
    }

    public boolean isBackActivated() {
        return IsBackActivated;
    }

    public boolean isFootActivated() {
        return IsFootActivated;
    }

    public boolean isSeatActivated() {
        return IsSeatActivated;
    }

    public boolean isLegActivated(){
        return IsLegActivated;
    }

    public boolean isAngleActivated() {
        return IsAngleActivated;
    }

    public boolean isWeightActivated(){
        return IsWeightActivated;
    }

    public void setSeatActivated(boolean seatActivated) {
        IsSeatActivated = seatActivated;
    }

    public void setLegActivated(boolean legActivated) {
        IsLegActivated = legActivated;
    }

    public void setFootActivated(boolean footActivated) {
        IsFootActivated = footActivated;
    }

    public void setAngleActivated(boolean angleActivated) {
        IsAngleActivated = angleActivated;
    }

    public void setWeightActivated(boolean weightActivated) {
        IsWeightActivated = weightActivated;
    }

    public void setBackActivated(boolean backActivated) {
        IsBackActivated = backActivated;
    }

    public ExerciseType type(){
        return Type;
    }

    public boolean isDeviceNumberActivated() {
        return IsDeviceNumberActivated;
    }

    public void setDeviceNumberActivated(boolean deviceNumberActivated) {
        IsDeviceNumberActivated = deviceNumberActivated;
    }

    public int getDeviceNumber() {
        return DeviceNumber;
    }

    public void setDeviceNumber(int deviceNumber) {
        DeviceNumber = deviceNumber;
    }

    public boolean isAdditionalWeightActivated() {
        return IsAdditionalWeightActivated;
    }

    public void setAdditionalWeightActivated(boolean additionalWeightActivated) {
        IsAdditionalWeightActivated = additionalWeightActivated;
    }

    public double getAdditionalWeight() {
        return AdditionalWeight;
    }

    public void setAdditionalWeight(double additionalWeight) {
        AdditionalWeight = additionalWeight;
    }
}
