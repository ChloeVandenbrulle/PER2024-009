package fr.inria.corese.demo.model.fileList;

/**
 * Représente un élément de fichier dans une liste de fichiers.
 *
 * Cette classe encapsule les informations de base d'un fichier,
 * notamment son nom et son état de chargement.
 *
 * Caractéristiques principales :
 * - Stockage du nom du fichier
 * - Suivi de l'état de chargement du fichier
 *
 * Utilisation typique dans la gestion de listes de fichiers
 * au sein d'une application de traitement de données.
 *
 * @author Clervie Causer
 * @version 1.0
 * @since 2025
 */
public class FileItem {
    private final String name;
    private boolean loading;

    /**
     * Constructeur pour créer un nouvel élément de fichier.
     *
     * @param name Le nom du fichier
     */
    public FileItem(String name) {
        this.name = name;
        this.loading = false;
    }

    /**
     * Récupère le nom du fichier.
     *
     * @return Le nom du fichier
     */
    public String getName() {
        return name;
    }

    /**
     * Vérifie si le fichier est en cours de chargement.
     *
     * @return true si le fichier est en cours de chargement, false sinon
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * Définit l'état de chargement du fichier.
     *
     * @param loading Nouvel état de chargement du fichier
     */
    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}