package com.german_software_engineers.trainerapp.Model;

public class Exercise {
    private String Name;

    private boolean IsSeatActivated;
    private int SeatPosition;
    private boolean IsLegActivated;
    private int LegPosition;
    private boolean IsFootActivated;
    private int FootPosition;
    private boolean IsAngleActivated;
    private int AnglePosition;
    private boolean IsWeightActivated;
    private double Weight;
    private boolean IsBackActivated;
    private int BackPosition;

    private Exercise(){}

    public Exercise(String name,boolean isSeatActivated, boolean isLegActivated, boolean isFootActivated, boolean isAngleActivated, boolean isWeightActivated, boolean isBackActivated){
        Name = name;
        IsSeatActivated = isSeatActivated;
        IsLegActivated = isLegActivated;
        IsFootActivated = isFootActivated;
        IsAngleActivated = isAngleActivated;
        IsWeightActivated = isWeightActivated;
        IsBackActivated = isBackActivated;
    }

    public void setSeatPosition(int pos){
        if(IsSeatActivated)
            SeatPosition = pos;
    }


    public int getSeatPosition() {
        if(IsSeatActivated)
            return SeatPosition;
        else
            return -1;
    }

    public int getLegPosition() {
        if(IsLegActivated)
            return LegPosition;
        else
            return -1;
    }

    public void setLegPosition(int legPosition) {
        if(IsLegActivated)
            LegPosition = legPosition;
    }

    public int getFootPosition() {
        if(IsFootActivated)
            return FootPosition;
        else
            return -1;
    }

    public void setFootPosition(int footPosition) {
        if(IsFootActivated)
            FootPosition = footPosition;
    }

    public int getAnglePosition() {
        if(IsAngleActivated)
            return AnglePosition;
        else
            return -1;
    }

    public void setAnglePosition(int anglePosition) {
        if(IsAngleActivated)
            AnglePosition = anglePosition;
    }

    public double getWeight() {
        if(IsWeightActivated)
            return Weight;
        else
            return -1;
    }

    public void setWeight(double weight) {
        if(IsWeightActivated)
            Weight = weight;
    }

    public String getName() {
        return Name;
    }

    public int getBackPosition() {
        if(IsBackActivated)
            return BackPosition;
        else
            return -1;
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

    public void copy(Exercise other){
        Name=other.Name;
        IsSeatActivated = other.IsSeatActivated;
        SeatPosition= other.SeatPosition;
        IsLegActivated= other.IsLegActivated;
        LegPosition= other.LegPosition;
        IsFootActivated= other.IsFootActivated;
        FootPosition= other.FootPosition;
        IsAngleActivated= other.IsAngleActivated;
        AnglePosition= other.AnglePosition;
        IsWeightActivated= other.IsWeightActivated;
        Weight= other.Weight;
        IsBackActivated= other.IsBackActivated;
        BackPosition= other.BackPosition;
    }

    @Override
    public String toString()
    {
        return Name;
    }

    @Override
    public boolean equals( Object obj )
    {
        return  (toString()==obj.toString());
    }
}
