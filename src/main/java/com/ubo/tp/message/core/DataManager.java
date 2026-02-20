package com.ubo.tp.message.core;

import com.ubo.tp.message.core.database.EntityManager;
import com.ubo.tp.message.core.database.IDatabase;
import com.ubo.tp.message.core.database.observer.IChannelDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.core.directory.IWatchableDirectory;
import com.ubo.tp.message.core.directory.WatchableDirectory;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.IMessageRecipient;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.logger.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Implémentation concrète de {@link IDataManager}.
 * <p>
 * Sert d'adaptateur entre la base en mémoire ({@link IDatabase}), le
 * {@link EntityManager} (responsable de la conversion fichier &lt;-&gt; entités)
 * et la surveillance du répertoire d'échange.
 * </p>
 * <p>
 * Responsabilités principales :
 * <ul>
 *   <li>Déléguer les requêtes de lecture à {@link IDatabase}.</li>
 *   <li>Déléguer l'écriture (publication) à {@link EntityManager} qui génère
 *       les fichiers de sortie.</li>
 *   <li>Configurer et démarrer la surveillance du répertoire d'échange via
 *       {@link WatchableDirectory}.</li>
 * </ul>
 * </p>
 */
public class DataManager implements IDataManager {

    /**
     * Base de donnée de l'application.
     */
    protected final IDatabase mDatabase;

    /**
     * Gestionnaire des entités contenu de la base de données.
     */
    protected final EntityManager mEntityManager;
    private final Logger logger;
    /**
     * Classe de surveillance de répertoire
     */
    protected IWatchableDirectory mWatchableDirectory;

    /**
     * Constructeur.
     *
     * @param database      base de données en mémoire
     * @param entityManager gestionnaire de conversion fichier/entité
     * @param logger        logger applicatif
     */
    public DataManager(IDatabase database, EntityManager entityManager, Logger logger) {
        mDatabase = database;
        mEntityManager = entityManager;
        this.logger = logger;
    }

    /**
     * {@inheritDoc}
     */
    public void addObserver(IDatabaseObserver observer) {
        this.mDatabase.addObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    public void removeObserver(IDatabaseObserver observer) {
        this.mDatabase.removeObserver(observer);
    }

    /**
     * Ajoute un observateur spécialisé pour les messages.
     *
     * @param observer observateur de messages
     */
    public void addObserver(IMessageDatabaseObserver observer) {
        this.mDatabase.addObserver(observer);
    }

    /**
     * Retire un observateur spécialisé pour les messages.
     *
     * @param observer observateur de messages
     */
    public void removeObserver(IMessageDatabaseObserver observer) {
        this.mDatabase.removeObserver(observer);
    }

    /**
     * Ajoute un observateur spécialisé pour les utilisateurs.
     *
     * @param observer observateur d'utilisateurs
     */
    public void addObserver(IUserDatabaseObserver observer) {
        this.mDatabase.addObserver(observer);
    }

    /**
     * Retire un observateur spécialisé pour les utilisateurs.
     *
     * @param observer observateur d'utilisateurs
     */
    public void removeObserver(IUserDatabaseObserver observer) {
        this.mDatabase.removeObserver(observer);
    }

    /**
     * Ajoute un observateur spécialisé pour les canaux.
     *
     * @param observer observateur de canaux
     */
    public void addObserver(IChannelDatabaseObserver observer) {
        this.mDatabase.addObserver(observer);
    }

    /**
     * Retire un observateur spécialisé pour les canaux.
     *
     * @param observer observateur de canaux
     */
    public void removeObserver(IChannelDatabaseObserver observer) {
        this.mDatabase.removeObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    public Set<User> getUsers() {
        return this.mDatabase.getUsers();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Message> getMessages() {
        return this.mDatabase.getMessages();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Channel> getChannels() {
        return this.mDatabase.getChannels();
    }

    /**
     * {@inheritDoc}
     */
    public void sendMessage(Message message) {
        // Ecrit un message
        this.mEntityManager.writeMessageFile(message);
    }

    /**
     * {@inheritDoc}
     */
    public void sendUser(User user) {
        // Ecrit un utilisateur
        this.mEntityManager.writeUserFile(user);
    }

    /**
     * {@inheritDoc}
     */
    public void sendChannel(Channel channel) {
        // Ecrit un canal
        this.mEntityManager.writeChannelFile(channel);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Message> getMessagesFrom(User user) {
        Set<Message> userMessages = new HashSet<>();

        // Parcours de tous les messages de la base
        for (Message message : this.getMessages()) {
            // Si le message est celui recherché
            if (message.getSender().equals(user)) {
                userMessages.add(message);
            }
        }

        return userMessages;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Message> getMessagesFrom(User sender, IMessageRecipient recipient) {
        Set<Message> userMessages = new HashSet<>();

        // Parcours de tous les messages de l'utilisateur
        for (Message message : this.getMessagesFrom(sender)) {
            // Si le message est celui recherché
            if (message.getRecipient().equals(recipient.getUuid())) {
                userMessages.add(message);
            }
        }

        return userMessages;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Message> getMessagesTo(User user) {
        Set<Message> userMessages = new HashSet<>();

        // Parcours de tous les messages de la base
        for (Message message : this.getMessages()) {
            // Si le message est celui recherché
            if (message.getSender().equals(user)) {
                userMessages.add(message);
            }
        }

        return userMessages;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Configure le répertoire d'échange côté gestionnaire d'entités et démarre
     * la surveillance des fichiers. Le {@link EntityManager} est enregistré comme
     * observateur du watcher pour synchroniser la base avec les fichiers.
     * </p>
     */
    public void setExchangeDirectory(String directoryPath) {
        logger.debug("DataManager : setExchangeDirectory : " + directoryPath);
        mEntityManager.setExchangeDirectory(directoryPath);

        mWatchableDirectory = new WatchableDirectory(directoryPath);
        // Enregistrer l'EntityManager avant d'initialiser la surveillance pour
        // récupérer immédiatement les fichiers présents via notifyPresentFiles
        mWatchableDirectory.addObserver(mEntityManager);
        mWatchableDirectory.initWatching();
    }
}
