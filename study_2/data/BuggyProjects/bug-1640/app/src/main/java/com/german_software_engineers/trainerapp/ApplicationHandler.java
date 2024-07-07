package com.german_software_engineers.trainerapp;


import com.german_software_engineers.trainerapp.Model.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

class ApplicationHandler {
    private static Model model = new Model();
    private static FileOutputStream FileOutput;
    private static FileInputStream FileInput;
    public static final String FileName = "Fitness.config";

    static public Model getModel()
    {
        return model;
    }

    static public void setFileContext(FileOutputStream fileOutput, FileInputStream fileInput){
        FileOutput = fileOutput;
        FileInput = fileInput;
    }

    static public void readSchedules(){
        model.readFile(FileInput);
    }

    static public void writeSchedules(){
        model.saveFile(FileOutput);
    }
}
