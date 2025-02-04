package fr.inria.corese.demo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;

public class ProjectDataModel {
    private ObservableList<String> fileList;
    private int semanticElementsCount;
    private int tripletCount;
    private int graphCount;
    private int rulesLoadedCount;
    private boolean myRuleFileEnabled;

    public ProjectDataModel() {
        fileList = FXCollections.observableArrayList();
    }

    public void loadProject(File directory) {
        // Implémenter le chargement du projet
    }

    public void saveProject(File file) {
        // Implémenter la sauvegarde du projet
    }

    public void clearGraph() {
        // Implémenter la suppression du graphe
    }

    public void reloadFiles() {
        // Implémenter le rechargement des fichiers
    }

    public void loadFile(File file) {
        // Implémenter le chargement d'un fichier
    }

    public void loadRuleFile(File file) {
        // Implémenter le chargement d'un fichier de règles
    }

    public void setMyRuleFileEnabled(boolean enabled) {
        this.myRuleFileEnabled = enabled;
    }

    // Getters
    public ObservableList<String> getFileList() { return fileList; }
    public int getSemanticElementsCount() { return semanticElementsCount; }
    public int getTripletCount() { return tripletCount; }
    public int getGraphCount() { return graphCount; }
    public int getRulesLoadedCount() { return rulesLoadedCount; }
    public boolean isMyRuleFileEnabled() { return myRuleFileEnabled; }
}