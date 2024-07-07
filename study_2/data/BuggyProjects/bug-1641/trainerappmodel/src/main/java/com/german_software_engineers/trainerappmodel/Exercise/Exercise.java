package com.german_software_engineers.trainerappmodel.Exercise;

import com.german_software_engineers.trainerappmodel.Enumerations.ExerciseType;

public abstract class Exercise {
    private String Name;
    private int Position;

    private Exercise(){}

    public Exercise(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
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

    public abstract ExerciseType type();

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        Position = position;
    }
}
