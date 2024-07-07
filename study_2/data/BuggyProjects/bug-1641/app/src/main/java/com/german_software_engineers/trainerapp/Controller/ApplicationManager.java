package com.german_software_engineers.trainerapp.Controller;

import android.app.Application;

import com.german_software_engineers.trainerapp.R;
import com.german_software_engineers.trainerappmodel.Model.Model;
import com.german_software_engineers.trainerappmodel.Model.XMLParser;

import java.io.File;

public class ApplicationManager extends Application {
    private Model ApplicationModel = null;

    @Override
    public void onCreate() {
        super.onCreate();
        createModelAndLoadFiles();
    }

    private void createModelAndLoadFiles() {
        ApplicationModel = new Model();
        XMLParser xmlParser = new XMLParser(ApplicationModel);
        File DataFile = new File(getFilesDir() + "/" + getString(R.string.DataFile));
        xmlParser.parseFile(DataFile);
    }

    public Model getApplicationModel(){
        return ApplicationModel;
    }

    public void saveFile(){
        XMLParser xmlParser = new XMLParser(ApplicationModel);
        File DataFile = new File(getFilesDir() + "/" + getString(R.string.DataFile));
        xmlParser.writeFile(DataFile);
    }

    @Override
    public void onTerminate(){
        saveFile();
        super.onTerminate();
    }
}
