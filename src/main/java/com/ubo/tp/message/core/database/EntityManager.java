package com.ubo.tp.message.core.database;

import com.ubo.tp.message.common.Constants;
import com.ubo.tp.message.common.DataFilesManager;
import com.ubo.tp.message.core.directory.IWatchableDirectoryObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

import java.io.File;
import java.util.*;

/**
 * Gestionnaire des entités et adaptateur entre le système de fichiers (répertoire
 * d'échange) et la base en mémoire {@link Database}.
 * <p>
 * Cette classe écoute les changements de fichiers (implémente
 * {@link IWatchableDirectoryObserver}) et convertit les fichiers en entités
 * (User, Message, Channel) pour les insérer/modifier/supprimer dans la base.
 * Elle expose également des opérations utilitaires pour écrire un message,
 * un utilisateur ou un canal dans le répertoire d'échange.
 * </p>
 * <p>
 * Remarque : les méthodes de parsing/écriture délèguent à {@link DataFilesManager}.
 * </p>
 */
public class EntityManager implements IWatchableDirectoryObserver {

    /**
     * Base de donnée de l'application.
     */
    protected final Database mDatabase;

    /**
     * Gestionnaire de fichier.
     */
    protected final DataFilesManager mDataFileManager;
    /**
     * Map reliant les UUID aux utilisateurs associés.
     */
    protected final Map<UUID, User> mUserMap;
    /**
     * Map reliant les noms de fichiers aux messages associés.
     */
    protected final Map<String, Message> mMessageFileMap;
    /**
     * Map reliant les noms de fichiers aux utilisateurs associés.
     */
    protected final Map<String, User> mUserFileMap;
    /**
     * Map reliant les noms de fichiers aux canaux associés.
     */
    protected final Map<String, Channel> mChannelFileMap;
    /**
     * Chemin d'accès au répertoire d'échange.
     */
    protected String mDirectoryPath;

    /**
     * Constructeur.
     */
    public EntityManager(Database database) {
        this.mDatabase = database;
        this.mUserMap = new HashMap<>();
        this.mMessageFileMap = new HashMap<>();
        this.mUserFileMap = new HashMap<>();
        this.mChannelFileMap = new HashMap<>();
        this.mDataFileManager = new DataFilesManager();

        // Ajout de l'utilisateur inconnu
        User unknowUser = Constants.UNKNOWN_USER;
        this.mUserMap.put(unknowUser.getUuid(), unknowUser);
        this.mDatabase.addUser(unknowUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyPresentFiles(Set<File> presentFiles) {
        // L'initialisation est une phase d'ajout massive
        this.notifyNewFiles(presentFiles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyNewFiles(Set<File> newFiles) {
        //
        // Récupération des fichiers utilisateurs en premier
        // (nécessaires pour gérer les messages)
        Set<File> userFiles = this.getUserFiles(newFiles);

        // Parcours de la liste des fichiers utilisateurs
        for (File userFile : userFiles) {

            // Extraction du nouvel utilisateur
            User newUser = this.extractUser(userFile);

            if (newUser != null) {
                // Si l'utilisateur existe déjà (UUID connu), on considère que c'est une modification
                if (this.mUserMap.containsKey(newUser.getUuid())) {
                    this.mDatabase.modifiyUser(newUser);
                } else {
                    // Ajout de l'utilisateur
                    this.mDatabase.addUser(newUser);
                }

                // Stockage dans les maps
                mUserMap.put(newUser.getUuid(), newUser);
                mUserFileMap.put(userFile.getName(), newUser);
            }
        }

        //
        // Récupération des fichiers de Messages.
        Set<File> messageFiles = this.getMessageFiles(newFiles);

        // Parcours de la liste des nouveaux messages
        for (File messageFile : messageFiles) {

            // Extraction du nouveau message
            Message newMessage = this.extractMessage(messageFile);

            if (newMessage != null) {
                // Si le message existe déjà (UUID connu), on considère que c'est une modification
                if (this.mMessageFileMap.containsValue(newMessage) || this.mMessageFileMap.containsKey(messageFile.getName())) {
                    // Utiliser modifiy si l'UUID est déjà présent dans la DB
                    this.mDatabase.modifiyMessage(newMessage);
                } else {
                    // Ajout du message
                    this.mDatabase.addMessage(newMessage);
                }

                // MAJ de la map
                this.mMessageFileMap.put(messageFile.getName(), newMessage);
            }
        }

        //
        // Récupération des fichiers de Messages.
        Set<File> channelFiles = this.getChannelFiles(newFiles);

        // Parcours de la liste des nouveaux canaux
        for (File channelFile : channelFiles) {

            // Extraction du nouveau cannal
            Channel newChannel = this.extractChannel(channelFile);

            if (newChannel != null) {
                if (this.mChannelFileMap.containsValue(newChannel) || this.mChannelFileMap.containsKey(channelFile.getName())) {
                    this.mDatabase.modifiyChannel(newChannel);
                } else {
                    // Ajout du message
                    this.mDatabase.addChannel(newChannel);
                }

                // MAJ de la map
                this.mChannelFileMap.put(channelFile.getName(), newChannel);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDeletedFiles(Set<File> deletedFiles) {
        //
        // Récupération des fichiers d'utilisateurs
        Set<File> userFiles = this.getUserFiles(deletedFiles);

        // Parcours de la liste des fichiers utilisateurs supprimés
        for (File deletedUserFile : userFiles) {

            // Récupération de l'utilisateur correspondant
            User deletedUser = this.mUserFileMap.get(deletedUserFile.getName());

            if (deletedUser != null) {
                // Suppression de l'utilisateur
                this.mDatabase.deleteUser(deletedUser);

                // MAJ des maps
                mUserMap.remove(deletedUser.getUuid());
                mUserFileMap.remove(deletedUserFile.getName());
            }
        }

        //
        // Récupération des fichiers message supprimés
        Set<File> deletedMessageFiles = this.getMessageFiles(deletedFiles);

        // Parcours de la liste des fichiers message supprimés
        for (File deletedMessageFile : deletedMessageFiles) {

            // Récupération du message correspondant
            Message deletedMessage = this.mMessageFileMap.get(deletedMessageFile.getName());

            if (deletedMessage != null) {
                // Suppression du message
                this.mDatabase.deleteMessage(deletedMessage);

                // MAJ de la map
                mMessageFileMap.remove(deletedMessageFile.getName());
            }
        }

        //
        // Récupération des fichiers canaux supprimés
        Set<File> deletedChannelFiles = this.getChanelFiles(deletedFiles);

        // Parcours de la liste des fichiers canaux supprimés
        for (File deletedChannelFile : deletedChannelFiles) {

            // Récupération du canal correspondant
            Channel deletedChannel = this.mChannelFileMap.get(deletedChannelFile.getName());

            if (deletedChannel != null) {
                // Suppression du canal
                this.mDatabase.deleteChannel(deletedChannel);

                // MAJ de la map
                mChannelFileMap.remove(deletedChannelFile.getName());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyModifiedFiles(Set<File> modifiedFiles) {
        //
        // Récupération des utilisateurs en premier (nécessaires pour gérer
        // les
        // message)
        Set<File> userFiles = this.getUserFiles(modifiedFiles);

        // Récupération et parcours de la liste des utilisateurs modifiés
        for (User modifiedUser : this.extractAllUsers(userFiles)) {
            // Modification de l'utilisateur
            this.mDatabase.modifiyUser(modifiedUser);

            // Stockage dans la map
            mUserMap.put(modifiedUser.getUuid(), modifiedUser);
        }

        //
        // Récupération des Message.
        Set<File> messageFiles = this.getMessageFiles(modifiedFiles);

        // Récupération et parcours de la liste des messages modifiés
        for (Message modifiedMessage : this.extractAllMessages(messageFiles)) {
            // Ajout du message
            this.mDatabase.modifiyMessage(modifiedMessage);
        }

        //
        // Récupération des canaux.
        Set<File> channelFiles = this.getChanelFiles(modifiedFiles);

        // Récupération et parcours de la liste des messages modifiés
        for (Channel modifiedChannel : this.extractAllChannel(channelFiles)) {
            // Ajout du message
            this.mDatabase.modifiyChannel(modifiedChannel);
        }
    }

    /**
     * Extraction de tous les message d'une liste de fichier.
     *
     * @param allMessageFiles , Liste des fichiers de message.
     */
    protected Set<Message> extractAllMessages(Set<File> allMessageFiles) {
        Set<Message> allMessages = new HashSet<>();

        // Parcours de tous les fichiers de message
        for (File messageFile : allMessageFiles) {
            // Extraction du message pour le fichier courant
            Message message = this.extractMessage(messageFile);

            // Si le message a bien été récupéré
            if (message != null) {
                // Sauvegarde de l'objet
                allMessages.add(message);
            }
        }

        return allMessages;
    }

    /**
     * Extraction de tous les canaux d'une liste de fichier.
     *
     * @param allChannelFiles , Liste des fichiers de cannaux.
     */
    protected Set<Channel> extractAllChannel(Set<File> allChannelFiles) {
        Set<Channel> allChannel = new HashSet<>();

        // Parcours de tous les fichiers de canaux
        for (File channelFile : allChannelFiles) {
            // Extraction du canal pour le fichier courant
            Channel channel = this.extractChannel(channelFile);

            // Si le canal a bien été récupéré
            if (channel != null) {
                // Sauvegarde de l'objet
                allChannel.add(channel);
            }
        }

        return allChannel;
    }

    /**
     * Extraction du fichier pour récupérer le Message correspondant. <br/>
     * <i>Retourne <code>null</code> si un problème est rencontré</i>.
     *
     * @param messageFile , Fichier properties du message à ectraire.
     */
    protected Message extractMessage(File messageFile) {
        return mDataFileManager.readMessage(messageFile, this.mUserMap);
    }

    /**
     * Extraction de tous les utilisateur d'une liste de fichier.
     *
     * @param allUserFiles , Liste des fichiers d'utilisateur.
     */
    protected Set<User> extractAllUsers(Set<File> allUserFiles) {
        Set<User> allUsers = new HashSet<>();

        // Parcours de tous les fichiers de l'utilsiateur
        for (File userFile : allUserFiles) {
            // Extraction de l'utilisateur pour le fichier courant
            User user = this.extractUser(userFile);

            // Si l'utilisateur a bien été récupéré
            if (user != null) {
                // Sauvegarde de l'objet
                allUsers.add(user);
            }
        }

        return allUsers;
    }

    /**
     * Extraction du fichier pour récupérer l'utilisateur correspondant. <br/>
     * <i>Retourne <code>null</code> si un problème est rencontré</i>.
     *
     * @param userFile , Fichier properties de l'utilisateur à ectraire.
     */
    protected User extractUser(File userFile) {
        return mDataFileManager.readUser(userFile);
    }

    /**
     * Extraction du fichier pour récupérer le canal correspondant. <br/>
     * <i>Retourne <code>null</code> si un problème est rencontré</i>.
     *
     * @param userFile , Fichier properties du canal à ectraire.
     */
    protected Channel extractChannel(File userFile) {
        return mDataFileManager.readChannel(userFile, mUserMap);
    }

    /**
     * Retourne la liste des fichiers de type 'Utilisateur' parmis la liste des
     * fichiers donnés.
     *
     * @param allFiles , Liste complète des fichiers.
     */
    protected Set<File> getUserFiles(Set<File> allFiles) {
        return this.getSpecificFiles(allFiles, Constants.USER_FILE_EXTENSION);
    }

    /**
     * Retourne la liste des fichiers de type 'Message' parmis la liste des fichiers
     * donnés.
     *
     * @param allFiles , Liste complète des fichiers.
     */
    protected Set<File> getMessageFiles(Set<File> allFiles) {
        return this.getSpecificFiles(allFiles, Constants.MESSAGE_FILE_EXTENSION);
    }

    /**
     * Retourne la liste des fichiers de type 'Chanel' parmis la liste des fichiers
     * donnés.
     *
     * @param allFiles , Liste complète des fichiers.
     */
    protected Set<File> getChanelFiles(Set<File> allFiles) {
        return this.getSpecificFiles(allFiles, Constants.CHANNEL_FILE_EXTENSION);
    }

    /**
     * Retourne la liste des fichiers de type 'Channel' parmis la liste des fichiers
     * donnés.
     *
     * @param allFiles , Liste complète des fichiers.
     */
    protected Set<File> getChannelFiles(Set<File> allFiles) {
        return this.getSpecificFiles(allFiles, Constants.CHANNEL_FILE_EXTENSION);
    }

    /**
     * Retourne la liste des fichiers ayant une extension particulière parmis la
     * liste des fichiers donnés.
     *
     * @param allFiles  , Liste complète des fichiers.
     * @param extension , Extension des fichiers à récupérer.
     */
    protected Set<File> getSpecificFiles(Set<File> allFiles, String extension) {
        Set<File> specificFiles = new HashSet<>();

        // Parcours de tous les fichiers donnés
        for (File file : allFiles) {
            // Si le fichier est un fichier ayant l'extension voulue
            if (file.getName().endsWith(extension)) {
                specificFiles.add(file);
            }
        }

        return specificFiles;
    }

    /**
     * Configure le chemin d'accès au répertoire d'échange.
     *
     * @param directoryPath chemin du répertoire d'échange (non-null)
     */
    public void setExchangeDirectory(String directoryPath) {
        this.mDirectoryPath = directoryPath;
        this.mDataFileManager.setExchangeDirectory(directoryPath);
    }

    /**
     * Génération/écriture du fichier correspondant au message.
     *
     * @param message message à écrire
     * @throws RuntimeException si le répertoire d'échange n'est pas configuré
     */
    public void writeMessageFile(Message message) {
        if (mDirectoryPath != null) {
            mDataFileManager.writeMessageFile(message);
        } else {
            throw new RuntimeException("Le répertoire d'échange n'est pas configuré !");
        }
    }

    /**
     * Génération/écriture du fichier correspondant à l'utilisateur.
     *
     * @param user utilisateur à écrire
     * @throws RuntimeException si le répertoire d'échange n'est pas configuré
     */
    public void writeUserFile(User user) {
        if (mDirectoryPath != null) {
            mDataFileManager.writeUserFile(user);
        } else {
            throw new RuntimeException("Le répertoire d'échange n'est pas configuré !");
        }
    }

    /**
     * Génération/écriture du fichier correspondant à un canal.
     *
     * @param channel canal à écrire
     * @throws RuntimeException si le répertoire d'échange n'est pas configuré
     */
    public void writeChannelFile(Channel channel) {
        if (mDirectoryPath != null) {
            mDataFileManager.writeChannelFile(channel);
        } else {
            throw new RuntimeException("Le répertoire d'échange n'est pas configuré !");
        }
    }

    /**
     * Supprime le fichier d'échange associé à un utilisateur.
     * Si le fichier est supprimé avec succès, met également à jour la base
     * et les maps internes.
     *
     * @param user utilisateur à supprimer
     * @return true si la suppression a été effectuée (fichier supprimé ou suppression en base effectuée), false sinon
     */
    public boolean deleteUserFile(User user) {
        if (user == null) return false;
        if (mDirectoryPath == null) return false;

        String fileName = user.getUuid().toString() + "." + Constants.USER_FILE_EXTENSION;
        File f = new File(this.mDirectoryPath + Constants.SYSTEM_FILE_SEPARATOR + fileName);

        boolean deleted = false;
        if (f.exists()) {
            try {
                deleted = f.delete();
            } catch (Exception ignored) {
                deleted = false;
            }
        }

        // Si le fichier a bien été supprimé (ou n'existait pas), on met à jour la base et les maps
        if (deleted || !f.exists()) {
            // suppression en base
            this.mDatabase.deleteUser(user);

            // MAJ des maps
            mUserMap.remove(user.getUuid());
            mUserFileMap.remove(fileName);

            return true;
        }

        return false;
    }

    /**
     * Supprime le fichier d'échange associé à un message.
     * Si le fichier est supprimé avec succès, met également à jour la base
     * et les maps internes.
     *
     * @param message message à supprimer
     * @return true si la suppression a été effectuée (fichier supprimé ou suppression en base effectuée), false sinon
     */
    public boolean deleteMessageFile(Message message) {
        if (message == null) return false;
        if (mDirectoryPath == null) return false;

        String fileName = message.getUuid().toString() + "." + Constants.MESSAGE_FILE_EXTENSION;
        File f = new File(this.mDirectoryPath + Constants.SYSTEM_FILE_SEPARATOR + fileName);

        boolean deleted = false;
        if (f.exists()) {
            try {
                deleted = f.delete();
            } catch (Exception ignored) {
                deleted = false;
            }
        }

        if (deleted || !f.exists()) {
            this.mDatabase.deleteMessage(message);
            mMessageFileMap.remove(fileName);
            return true;
        }

        return false;
    }

    /**
     * Supprime le fichier d'échange associé à un canal.
     * Si le fichier est supprimé avec succès, met également à jour la base
     * et les maps internes.
     *
     * @param channel canal à supprimer
     * @return true si la suppression a été effectuée (fichier supprimé ou suppression en base effectuée), false sinon
     */
    public boolean deleteChannelFile(Channel channel) {
        if (channel == null) return false;
        if (mDirectoryPath == null) return false;

        String fileName = channel.getUuid().toString() + "." + Constants.CHANNEL_FILE_EXTENSION;
        File f = new File(this.mDirectoryPath + Constants.SYSTEM_FILE_SEPARATOR + fileName);

        boolean deleted = false;
        if (f.exists()) {
            try {
                deleted = f.delete();
            } catch (Exception ignored) {
                deleted = false;
            }
        }

        if (deleted || !f.exists()) {
            this.mDatabase.deleteChannel(channel);
            mChannelFileMap.remove(fileName);
            return true;
        }

        return false;
    }

}
