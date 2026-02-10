package com.ubo.tp.message.core.directory;

import java.io.File;
import java.util.Set;

/**
 * Observateur réagissant aux événements de changement d'un répertoire surveillé.
 * <p>
 * Les notifications peuvent provenir d'un thread de surveillance ; les
 * implémentations doivent gérer correctement l'ordonnancement si elles
 * effectuent des mises à jour d'interface utilisateur.
 * </p>
 */
public interface IWatchableDirectoryObserver {

    /**
     * Notification de la liste des fichiers présents initialement dans le
     * répertoire.
     *
     * @param presentFiles ensemble des fichiers initialement présents
     */
    void notifyPresentFiles(Set<File> presentFiles);

    /**
     * Notification de la liste des nouveaux fichiers dans le répertoire.
     *
     * @param newFiles nouveaux fichiers détectés
     */
    void notifyNewFiles(Set<File> newFiles);

    /**
     * Notification de la liste des fichiers supprimés dans le répertoire.
     *
     * @param deletedFiles fichiers supprimés
     */
    void notifyDeletedFiles(Set<File> deletedFiles);

    /**
     * Notification de la liste des fichiers modifiés dans le répertoire.
     *
     * @param modifiedFiles fichiers modifiés
     */
    void notifyModifiedFiles(Set<File> modifiedFiles);
}
