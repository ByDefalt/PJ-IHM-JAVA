package com.ubo.tp.message.core.directory;

/**
 * Interface de l'entité responsable de la surveillance d'un répertoire.
 * <p>
 * Fournit un mécanisme pour démarrer/arrêter la surveillance et pour changer
 * le répertoire surveillé. Les observateurs enregistrés seront notifiés de la
 * liste initiale des fichiers présents puis des changements (ajout/suppression/modifications).
 * </p>
 */
public interface IWatchableDirectory {

    /**
     * Initialisation de la surveillance du répertoire.
     * <p>
     * Les observeurs sont d'abord avertis du contenu initial, puis des modifications
     * survenues par la suite.
     * </p>
     */
    void initWatching();

    /**
     * Arrêt de la surveillance du répertoire.
     */
    void stopWatching();

    /**
     * Change le répertoire surveillé. Les fichiers précédemment présents seront
     * considérés comme supprimés et les observeurs en seront informés.
     *
     * @param directoryPath chemin absolu du répertoire à surveiller
     */
    void changeDirectory(String directoryPath);

    /**
     * Ajoute un observateur qui sera notifié des événements (présent, ajout, suppression, modification).
     *
     * @param observer observateur à ajouter
     */
    void addObserver(IWatchableDirectoryObserver observer);

    /**
     * Retire un observateur précédemment ajouté.
     *
     * @param observer observateur à retirer
     */
    void removeObserver(IWatchableDirectoryObserver observer);

}
