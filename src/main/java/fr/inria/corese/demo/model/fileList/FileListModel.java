package fr.inria.corese.demo.model.fileList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Modèle de gestion d'une liste de fichiers dans une application.
 *
 * Cette classe fournit une liste observable de fichiers avec des méthodes
 * de manipulation de base.
 *
 * Caractéristiques principales :
 * - Stockage de fichiers comme des éléments observables
 * - Opérations de base : ajout, suppression, effacement
 *
 * Utilisation typique dans les interfaces utilisateur JavaFX
 * pour gérer dynamiquement une liste de fichiers.
 *
 * @author Clervie Causer
 * @version 1.0
 * @since 2025
 */
public class FileListModel {
    private final ObservableList<FileItem> files = FXCollections.observableArrayList();

    /**
     * Récupère la liste observable des fichiers.
     *
     * @return Liste des fichiers sous forme observable
     */
    public ObservableList<FileItem> getFiles() {
        return files;
    }

    /**
     * Ajoute un nouveau fichier à la liste.
     *
     * Crée un nouvel élément de fichier avec le nom spécifié
     * et l'ajoute à la liste.
     *
     * @param name Nom du fichier à ajouter
     */
    public void addFile(String name) {
        System.out.println("Adding file to FileListModel: " + name);
        files.add(new FileItem(name));
    }

    /**
     * Supprime un fichier spécifique de la liste.
     *
     * @param file Élément de fichier à supprimer
     */
    public void removeFile(FileItem file) {
        files.remove(file);
    }

    /**
     * Efface tous les fichiers de la liste.
     *
     * Supprime tous les éléments de la liste de fichiers.
     */
    public void clearFiles() {
        files.clear();
    }
}