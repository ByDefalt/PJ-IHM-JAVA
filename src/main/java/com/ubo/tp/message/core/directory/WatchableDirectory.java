package com.ubo.tp.message.core.directory;

import java.io.File;
import java.util.*;

/**
 * Classe responsable de la surveillance d'un répertoire et de la notification
 * des {@link IWatchableDirectoryObserver} lors des changements.
 * <p>
 * Implémentation simple par polling. Un thread interne effectue des relevés
 * périodiques et notifie les observateurs des fichiers présents/ajoutés/supprimés
 * ou modifiés.
 * </p>
 * <p>
 * Attention : le polling s'effectue sur un thread dédié, les observers peuvent
 * donc être notifiés hors de l'EDT. Les observers doivent gérer la bascule
 * vers l'EDT si nécessaire pour mettre à jour l'interface graphique.
 * </p>
 */
public class WatchableDirectory implements IWatchableDirectory {

    /**
     * Temps (en ms) entre deux vérification du répertoire.
     */
    protected static final int POLLING_TIME = 1000;
    /**
     * Liste des observeurs sur le contenu du répertoire.
     */
    protected final Set<IWatchableDirectoryObserver> mObservers;
    /**
     * Chemin d'accès au repertoire à surveiller.
     */
    protected String mDirectoryPath;
    /**
     * Répertoire surveillé.
     */
    protected File mDirectory;
    /**
     * Liste des fichiers présents.
     */
    protected Set<File> mPresentFiles;
    /**
     * Map permettant de stocker les dates de modifications des fichiers.
     */
    protected Map<String, Long> mFileModificationMap;
    /**
     * Thread de surveillance du répertoire.
     */
    protected Thread mWatchingThread;

    /**
     * Constructeur.
     *
     * @param directoryPath Chemin d'accès au repertoire à surveiller.
     */
    public WatchableDirectory(String directoryPath) {
        this.mDirectoryPath = directoryPath;
        this.mPresentFiles = new HashSet<>();
        this.mFileModificationMap = new HashMap<>();
        this.mObservers = new HashSet<>();
    }

    /**
     * Change le répertoire de surveillance.
     * <p>
     * L'appel stoppe la surveillance courante, notifie les observers que les fichiers
     * précédemment présents ont été supprimés, puis remplace le chemin interne.
     * Un appel à {@link #initWatching()} est nécessaire pour relancer la surveillance
     * sur le nouveau répertoire.
     * </p>
     *
     * @param directoryPath nouveau répertoire à surveiller (chemin absolu)
     */
    @Override
    public void changeDirectory(String directoryPath) {
        // Clonage de la liste pour notification
        HashSet<File> presentFiles = new HashSet<>(this.mPresentFiles);

        // Arret de la surveillance en cours
        this.stopWatching();

        // Notification de la suppression des fichiers
        if (!presentFiles.isEmpty()) {
            this.notifyDeletedFiles(presentFiles);
        }

        // Réinit du répertoire de surveillance.
        this.mDirectoryPath = directoryPath;
    }

    /**
     * Initialise la surveillance du répertoire configuré.
     * <p>
     * Si le répertoire est valide, la méthode initialise l'état interne des fichiers
     * présents puis démarre le thread de polling. En cas d'erreur (répertoire
     * non existant), la surveillance n'est pas démarrée.
     * </p>
     */
    @Override
    public void initWatching() {
        // Chargement du répertoire
        mDirectory = new File(mDirectoryPath);

        // Si le répertoire est valide
        if (mDirectory.exists() && mDirectory.isDirectory()) {
            // Initialisation des fichiers présents
            this.initPresentFiles();

            // Démarrage de la surveillance
            this.startPolling();
        } else {
            System.err.println(
                    "Erreur lors du démarrage de la surveillance du répertoire : " + mDirectory.getAbsolutePath());
            mDirectory = null;
        }
    }

    /**
     * Ajoute un fichier à la liste des fichiers présents et met à jour sa date
     * de modification interne.
     *
     * @param fileToAdd fichier à ajouter
     */
    protected void addPresentFile(File fileToAdd) {
        // Ajout du fichier
        this.mPresentFiles.add(fileToAdd);

        // Stockage de la date de modification
        this.mFileModificationMap.put(fileToAdd.getName(), fileToAdd.lastModified());
    }

    /**
     * Initialisation de la liste des fichiers présents (et notification aux
     * observateurs).
     */
    protected void initPresentFiles() {
        if (mDirectory != null) {
            File[] files = mDirectory.listFiles();
            if (files == null || files.length == 0) return;
            Collections.addAll(this.mPresentFiles, files);

            // Notification de la liste des fichiers présents
            if (!this.mPresentFiles.isEmpty()) {
                this.notifyPresentFiles(this.mPresentFiles);
            }
        }
    }

    /**
     * Démarre le thread de polling qui appelle périodiquement {@link #watchDirectory()}.
     * <p>
     * Ce thread relance automatiquement une nouvelle itération après chaque pause.
     * </p>
     */
    protected void startPolling() {
        // If already running, don't start another watcher
        if (mWatchingThread != null && mWatchingThread.isAlive()) return;

        // Start a virtual thread that loops until interrupted
        mWatchingThread = Thread.ofVirtual().name("watchable-dir", 0).start(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(POLLING_TIME);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }

                try {
                    watchDirectory();
                } catch (Exception t) {
                    // Catch Throwable to avoid silent thread death; log and continue
                    System.err.println("Erreur lors de la surveillance du répertoire: " + t.getMessage());
                    t.printStackTrace(System.err);
                }
            }
            // Thread termine proprement
        });
    }

    /**
     * Effectue une étape de surveillance : détecte nouveaux, supprimés et
     * modifiés puis notifie-les observers.
     * <p>
     * Méthode protégée, car elle est appelée par le thread interne. Ne doit pas
     * être appelée directement depuis d'autres threads sans synchronisation.
     * </p>
     */
    protected void watchDirectory() {
        if (mDirectory == null) return;
        Set<File> presentFiles = listPresentFiles();
        processSnapshot(presentFiles);
    }

    /**
     * Traite un snapshot du répertoire : détection des nouveaux / supprimés / modifiés,
     * notification des observers et mise à jour de l'état interne.
     * <p>
     * Séparé de watchDirectory pour réduire la complexité cognitive et faciliter les tests.
     * </p>
     */
    protected void processSnapshot(Set<File> presentFiles) {
        // Ancien état
        Set<File> oldFiles = new HashSet<>(this.mPresentFiles);

        // Détection des changements
        Set<File> newFiles = detectNewFiles(presentFiles, oldFiles);
        Set<File> deletedFiles = detectDeletedFiles(presentFiles, oldFiles);
        Set<File> modifiedFiles = detectModifiedFiles(presentFiles, newFiles);

        // Notification
        notifyChanges(deletedFiles, newFiles, modifiedFiles);

        // Mise à jour interne de l'état
        updateInternalState(presentFiles);
    }

    /**
     * Récupère l'ensemble des fichiers présents actuellement dans le répertoire.
     */
    protected Set<File> listPresentFiles() {
        File[] files = mDirectory.listFiles();
        if (files == null) return Collections.emptySet();
        Set<File> presentFiles = new HashSet<>();
        Collections.addAll(presentFiles, files);
        return presentFiles;
    }

    /**
     * Détecte les nouveaux fichiers (présents maintenant mais pas dans l'ancien état).
     */
    protected Set<File> detectNewFiles(Set<File> presentFiles, Set<File> oldFiles) {
        Set<File> newFiles = new HashSet<>();
        for (File presentFile : presentFiles) {
            if (!oldFiles.contains(presentFile)) {
                newFiles.add(presentFile);
            }
        }
        return newFiles;
    }

    /**
     * Détecte les fichiers supprimés (présents dans l'ancien état mais pas maintenant).
     */
    protected Set<File> detectDeletedFiles(Set<File> presentFiles, Set<File> oldFiles) {
        Set<File> deletedFiles = new HashSet<>();
        for (File oldFile : oldFiles) {
            if (!presentFiles.contains(oldFile)) {
                deletedFiles.add(oldFile);
            }
        }
        return deletedFiles;
    }

    /**
     * Détecte les fichiers modifiés (présents et ayant une date de modification plus
     * récente que celle stockée), en ignorant les nouveaux fichiers.
     */
    protected Set<File> detectModifiedFiles(Set<File> presentFiles, Set<File> newFiles) {
        Set<File> modifiedFiles = new HashSet<>();
        for (File presentFile : presentFiles) {
            // Ignorer les fichiers nouvellement ajoutés
            if (newFiles.contains(presentFile)) continue;

            Long savedLastModification = mFileModificationMap.get(presentFile.getName());
            if (savedLastModification != null && savedLastModification < presentFile.lastModified()) {
                modifiedFiles.add(presentFile);
            }
        }
        return modifiedFiles;
    }

    /**
     * Envoie les notifications appropriées aux observers si les ensembles ne sont pas vides.
     */
    protected void notifyChanges(Set<File> deletedFiles, Set<File> newFiles, Set<File> modifiedFiles) {
        if (!deletedFiles.isEmpty()) this.notifyDeletedFiles(deletedFiles);
        if (!newFiles.isEmpty()) this.notifyNewFiles(newFiles);
        if (!modifiedFiles.isEmpty()) this.notifyModifiedFiles(modifiedFiles);
    }

    /**
     * Met à jour l'état interne des fichiers présents et des dates de modification.
     */
    protected void updateInternalState(Set<File> presentFiles) {
        this.mPresentFiles.clear();
        this.mFileModificationMap.clear();
        for (File file : presentFiles) this.addPresentFile(file);
    }

    /**
     * Arrête la surveillance et réinitialise l'état interne des fichiers.
     */
    @Override
    public void stopWatching() {
        if (this.mWatchingThread != null) {
            this.mWatchingThread.interrupt();
        }
        this.mPresentFiles.clear();
    }

    /**
     * Ajoute un observateur. Si le répertoire a déjà des fichiers présents,
     * il recevra immédiatement une notification {@link IWatchableDirectoryObserver#notifyPresentFiles}.
     *
     * @param observer observateur à ajouter (non-null)
     */
    @Override
    public void addObserver(IWatchableDirectoryObserver observer) {
        // Notification initiale du contenu
        if (!this.mPresentFiles.isEmpty()) {
            observer.notifyPresentFiles(this.mPresentFiles);
        }

        this.mObservers.add(observer);
    }

    /**
     * Retire un observateur pour qu'il ne reçoive plus de notifications.
     *
     * @param observer observateur à retirer
     */
    @Override
    public void removeObserver(IWatchableDirectoryObserver observer) {
        this.mObservers.remove(observer);
    }

    /**
     * Notification de la liste des fichiers présents initialement dans le
     * répertoire.
     *
     * @param presentFiles ensemble des fichiers présents
     */
    protected void notifyPresentFiles(Set<File> presentFiles) {
        // Clonage de la liste pour éviter les modifications concurrentes
        Set<IWatchableDirectoryObserver> clonedList = new HashSet<>(this.mObservers);

        for (IWatchableDirectoryObserver observer : clonedList) {
            observer.notifyPresentFiles(presentFiles);
        }
    }

    /**
     * Notification de la liste des nouveaux fichiers dans le répertoire.
     *
     * @param newFiles ensemble des fichiers nouvellement ajoutés
     */
    protected void notifyNewFiles(Set<File> newFiles) {
        // Clonage de la liste pour éviter les modifications concurrentes
        Set<IWatchableDirectoryObserver> clonedList = new HashSet<>(this.mObservers);

        for (IWatchableDirectoryObserver observer : clonedList) {
            observer.notifyNewFiles(newFiles);
        }
    }

    /**
     * Notification de la liste des fichiers supprimés dans le répertoire.
     *
     * @param deletedFiles ensemble des fichiers supprimés
     */
    protected void notifyDeletedFiles(Set<File> deletedFiles) {
        // Clonage de la liste pour éviter les modifications concurrentes
        Set<IWatchableDirectoryObserver> clonedList = new HashSet<>(this.mObservers);

        for (IWatchableDirectoryObserver observer : clonedList) {
            observer.notifyDeletedFiles(deletedFiles);
        }
    }

    /**
     * Notification de la liste des fichiers modifiés dans le répertoire.
     *
     * @param modifiedFiles ensemble des fichiers modifiés
     */
    protected void notifyModifiedFiles(Set<File> modifiedFiles) {
        // Clonage de la liste pour éviter les modifications concurrentes
        Set<IWatchableDirectoryObserver> clonedList = new HashSet<>(this.mObservers);

        for (IWatchableDirectoryObserver observer : clonedList) {
            observer.notifyModifiedFiles(modifiedFiles);
        }
    }
}
