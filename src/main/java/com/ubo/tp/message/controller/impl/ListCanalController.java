package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.common.Constants;
import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListCanalController;
import com.ubo.tp.message.core.database.observer.IChannelDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListCanalGraphicController;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListCanalGraphicController.ChannelEditCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Contrôleur de la liste des canaux (channels) affichée dans l'IHM.
 * Gère l'ajout/suppression/modification des canaux et la construction des
 * callbacks d'édition pour les canaux privés.
 */
public class ListCanalController implements IListCanalController, IChannelDatabaseObserver, IUserDatabaseObserver, IMessageDatabaseObserver {

    private final ControllerContext context;
    private final IListCanalGraphicController graphicController;

    /**
     * Crée une instance du contrôleur de canaux.
     *
     * @param context          contexte applicatif
     * @param graphicController contrôleur graphique associé
     */
    public ListCanalController(ControllerContext context, IListCanalGraphicController graphicController) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        this.context.dataManager().addObserver((IChannelDatabaseObserver) this);
        this.context.dataManager().addObserver((IUserDatabaseObserver) this);
        this.context.dataManager().addObserver((IMessageDatabaseObserver) this);

        refreshFormUsers();
    }

    /**
     * Rafraîchit la liste d'utilisateurs utilisée pour créer un nouveau canal.
     */
    private void refreshFormUsers() {
        List<User> usersWithoutMe = getUsersWithoutMe();
        this.graphicController.setupNewChannelForm(usersWithoutMe, this::createNewChannel);
    }

    /**
     * Retourne la liste des utilisateurs à inviter (sans l'utilisateur courant).
     *
     * @return liste d'utilisateurs
     */
    private List<User> getUsersWithoutMe() {
        Set<User> all = context.dataManager().getUsers();
        User me = context.session().getConnectedUser();
        List<User> result = new ArrayList<>();
        for (User u : all) {
            if (!u.equals(me) && !u.getUuid().equals(Constants.UNKNONWN_USER_UUID)) result.add(u);
        }
        return result;
    }

    /**
     * Définit le canal sélectionné et efface son badge de non lus.
     *
     * @param channel canal sélectionné
     */
    private void setSelected(Channel channel) {
        if (context.logger() != null) context.logger().debug("Canal sélectionné : " + channel);
        context.selected().setSelectedChannel(channel);
        graphicController.clearUnread(channel);
    }

    /**
     * Notification : un canal a été ajouté.
     *
     * @param addedChannel canal ajouté
     */
    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        handleNotifyChannelAddedLogic(addedChannel);
    }

    /**
     * Logique interne pour traiter l'ajout d'un canal.
     *
     * @param addedChannel canal ajouté
     */
    private void handleNotifyChannelAddedLogic(Channel addedChannel) {
        if (context.logger() != null) context.logger().debug("Canal ajouté : " + addedChannel);
        User me = context.session().getConnectedUser();

        if (shouldIgnorePrivateChannel(addedChannel, me)) {
            if (context.logger() != null)
                context.logger().debug("Ignorer le canal privé qui ne m'inclut pas : " + addedChannel);
            return;
        }

        boolean isOwner = isOwnerOfChannel(addedChannel, me);
        ChannelEditCallback onEdit = addedChannel.isPrivate() ? buildEditCallback(addedChannel, me, isOwner) : null;

        this.graphicController.addCanal(addedChannel, this::setSelected, onEdit, isOwner, this::getUsersWithoutMe);
    }

    /**
     * Indique si un canal privé doit être ignoré pour l'utilisateur courant.
     *
     * @param channel canal à tester
     * @param me      utilisateur courant
     * @return true si le canal doit être ignoré
     */
    private boolean shouldIgnorePrivateChannel(Channel channel, User me) {
        if (channel == null) return true;
        return channel.isPrivate()
                && (me == null || (!channel.getUsers().contains(me) && !channel.getCreator().equals(me)));
    }

    /**
     * Indique si l'utilisateur courant est le propriétaire du canal.
     *
     * @param channel canal examiné
     * @param me      utilisateur courant
     * @return true si me est le créateur
     */
    private boolean isOwnerOfChannel(Channel channel, User me) {
        return channel != null && channel.isPrivate() && Objects.equals(channel.getCreator(), me);
    }

    /**
     * Construit le callback d'édition pour un canal privé.
     *
     * @param channel canal cible
     * @param me      utilisateur courant
     * @param isOwner indique si l'utilisateur est propriétaire
     * @return callback d'édition
     */
    private ChannelEditCallback buildEditCallback(Channel channel, User me, boolean isOwner) {
        if (context.logger() != null) context.logger().debug("buildEditCallback for channel: " + channel + " owner? " + isOwner + " me=" + (me != null ? me.getName() : "null"));
        return new ChannelEditCallback() {
            @Override
            public void onLeave(Channel c) {
                if (context.logger() != null) context.logger().debug("onLeave called for: " + c + " (template: " + channel + ")");
                leaveChannel(c);
            }

            @Override
            public void onDelete(Channel c) {
                if (context.logger() != null) context.logger().debug("onDelete called for: " + c + " (template: " + channel + ")");
                deleteChannel(c);
            }

            @Override
            public void onAddUser(Channel c, User user) {
                if (context.logger() != null) context.logger().debug("onAddUser called for: " + c + " user=" + user);
                addUserToChannel(c, user);
            }

            @Override
            public void onRemoveUser(Channel c, User user) {
                if (context.logger() != null) context.logger().debug("onRemoveUser called for: " + c + " user=" + user);
                removeUserFromChannel(c, user);
            }
        };
    }

    /**
     * Notification : un canal a été supprimé.
     *
     * @param deletedChannel canal supprimé
     */
    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        handleNotifyChannelDeletedLogic(deletedChannel);
    }

    /**
     * Logique interne pour la suppression d'un canal.
     *
     * @param deletedChannel canal supprimé
     */
    private void handleNotifyChannelDeletedLogic(Channel deletedChannel) {
        if (context.logger() != null) context.logger().debug("Canal supprimé : " + deletedChannel);
        this.graphicController.removeCanal(deletedChannel);
    }

    /**
     * Notification : un canal a été modifié.
     *
     * @param modifiedChannel canal modifié
     */
    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        handleNotifyChannelModifiedLogic(modifiedChannel);
    }

    /**
     * Logique interne pour la modification d'un canal.
     *
     * @param modifiedChannel canal modifié
     */
    private void handleNotifyChannelModifiedLogic(Channel modifiedChannel) {
        if (context.logger() != null) context.logger().debug("Canal modifié : " + modifiedChannel);
        User me = context.session().getConnectedUser();

        if (modifiedChannel.isPrivate()
                && !modifiedChannel.getCreator().equals(me)
                && !modifiedChannel.getUsers().contains(me)) {
            this.graphicController.removeCanal(modifiedChannel);
        } else {
            boolean isOwner = isOwnerOfChannel(modifiedChannel, me);
            IListCanalGraphicController.ChannelEditCallback onEdit = modifiedChannel.isPrivate() ?
                    buildEditCallback(modifiedChannel, me, isOwner) : null;
            this.graphicController.addCanal(modifiedChannel, this::setSelected, onEdit, isOwner, this::getUsersWithoutMe);

            this.graphicController.updateCanal(modifiedChannel);
        }
    }

    /**
     * Notification : un utilisateur a été ajouté.
     *
     * @param addedUser utilisateur ajouté
     */
    @Override
    public void notifyUserAdded(User addedUser) { handleNotifyUserAddedLogic(addedUser); }

    /**
     * Logique interne pour la notification d'ajout d'utilisateur.
     *
     * @param addedUser utilisateur ajouté
     */
    private void handleNotifyUserAddedLogic(User addedUser) { if (context.logger() != null) { context.logger().debug("notifyUserAdded: " + addedUser); } refreshFormUsers(); }

    /**
     * Notification : un utilisateur a été supprimé.
     *
     * @param deletedUser utilisateur supprimé
     */
    @Override
    public void notifyUserDeleted(User deletedUser) { handleNotifyUserDeletedLogic(deletedUser); }

    /**
     * Logique interne pour la suppression d'un utilisateur.
     *
     * @param deletedUser utilisateur supprimé
     */
    private void handleNotifyUserDeletedLogic(User deletedUser) { if (context.logger() != null) { context.logger().debug("notifyUserDeleted: " + deletedUser); } refreshFormUsers(); }

    /**
     * Notification : un utilisateur a été modifié.
     *
     * @param modifiedUser utilisateur modifié
     */
    @Override
    public void notifyUserModified(User modifiedUser) { handleNotifyUserModifiedLogic(modifiedUser); }

    /**
     * Logique interne pour la modification d'un utilisateur.
     *
     * @param modifiedUser utilisateur modifié
     */
    private void handleNotifyUserModifiedLogic(User modifiedUser) { if (context.logger() != null) { context.logger().debug("notifyUserModified: " + modifiedUser); } refreshFormUsers(); }

    /**
     * Crée un nouveau canal: wrapper public.
     *
     * @param channelName  nom du canal
     * @param isPrivate    bool privé
     * @param invitedUsers liste d'invités
     */
    public void createNewChannel(String channelName, boolean isPrivate, List<User> invitedUsers) {
        handleCreateNewChannelLogic(channelName, isPrivate, invitedUsers);
    }

    /**
     * Logique interne pour la création d'un canal.
     *
     * @param channelName  nom du canal
     * @param isPrivate    bool privé
     * @param invitedUsers liste d'invités
     */
    private void handleCreateNewChannelLogic(String channelName, boolean isPrivate, List<User> invitedUsers) {
        Channel newChannel;
        if (invitedUsers != null && !invitedUsers.isEmpty()) {
            List<User> members = new ArrayList<>(invitedUsers);
            newChannel = new Channel(context.session().getConnectedUser(), channelName, members);
        } else {
            newChannel = new Channel(context.session().getConnectedUser(), channelName, isPrivate);
        }
        context.dataManager().sendChannel(newChannel);
        if (context.logger() != null) context.logger().debug("Création d'un nouveau canal : " + newChannel);
    }

    /**
     * Supprime définitivement le canal (wrapper public).
     *
     * @param channel canal à supprimer
     */
    public void deleteChannel(Channel channel) {
        handleDeleteChannelLogic(channel);
    }

    /**
     * Logique interne de suppression d'un canal.
     *
     * @param channel canal à supprimer
     */
    private void handleDeleteChannelLogic(Channel channel) {
        if (channel == null) return;
        context.dataManager().deleteChannelFile(channel);
        if (context.logger() != null)
            context.logger().debug("Canal supprimé par son créateur : " + channel.getName());
    }

    /**
     * Quitte le canal (wrapper public).
     *
     * @param channel canal à quitter
     */
    public void leaveChannel(Channel channel) {
        handleLeaveChannelLogic(channel);
    }

    /**
     * Logique interne pour quitter un canal.
     *
     * @param channel canal visé
     */
    private void handleLeaveChannelLogic(Channel channel) {
        if (channel == null) return;
        User me = context.session().getConnectedUser();
        List<User> newMembers = new ArrayList<>(channel.getUsers());
        newMembers.remove(me);
        Channel updated = new Channel(channel.getUuid(), channel.getCreator(), channel.getName(), newMembers, true);
        context.dataManager().sendChannel(updated);
        if (context.logger() != null)
            context.logger().debug("Quitté le canal : " + channel.getName());
    }

    /**
     * Ajoute un utilisateur au canal (wrapper public).
     *
     * @param channel canal ciblé
     * @param user    utilisateur à ajouter
     */
    public void addUserToChannel(Channel channel, User user) {
        handleAddUserToChannelLogic(channel, user);
    }

    /**
     * Logique interne d'ajout d'utilisateur au canal.
     *
     * @param channel canal ciblé
     * @param user    utilisateur à ajouter
     */
    private void handleAddUserToChannelLogic(Channel channel, User user) {
        if (channel == null || user == null) return;
        List<User> newMembers = new ArrayList<>(channel.getUsers());
        if (newMembers.contains(user)) return;
        newMembers.add(user);
        Channel updated = new Channel(channel.getUuid(), channel.getCreator(), channel.getName(), newMembers, true);
        graphicController.updateCanal(updated);
        context.dataManager().sendChannel(updated);
        if (context.logger() != null)
            context.logger().debug("Utilisateur ajouté au canal " + channel.getName() + " : " + user.getName());
    }

    /**
     * Retire un utilisateur du canal (wrapper public).
     *
     * @param channel canal ciblé
     * @param user    utilisateur à retirer
     */
    public void removeUserFromChannel(Channel channel, User user) {
        handleRemoveUserFromChannelLogic(channel, user);
    }

    /**
     * Logique interne pour retirer un utilisateur d'un canal.
     *
     * @param channel canal ciblé
     * @param user    utilisateur à retirer
     */
    private void handleRemoveUserFromChannelLogic(Channel channel, User user) {
        if (channel == null || user == null) return;
        List<User> newMembers = new ArrayList<>(channel.getUsers());
        if (!newMembers.contains(user)) return;
        newMembers.remove(user);
        Channel updated = new Channel(channel.getUuid(), channel.getCreator(), channel.getName(), newMembers, true);
        graphicController.updateCanal(updated);
        context.dataManager().sendChannel(updated);
        if (context.logger() != null)
            context.logger().debug("Utilisateur retiré du canal " + channel.getName() + " : " + user.getName());
    }

    /**
     * Notification : message ajouté (pour badge canal).
     *
     * @param addedMessage message ajouté
     */
    @Override
    public void notifyMessageAdded(Message addedMessage) {
        handleNotifyMessageAddedLogic(addedMessage);
    }

    /**
     * Logique interne lorsque un message est ajouté.
     *
     * @param addedMessage message ajouté
     */
    private void handleNotifyMessageAddedLogic(Message addedMessage) {
        if (addedMessage == null) return;

        Channel selectedChannel = context.selected().getSelectedChannel();
        UUID recipientUuid = addedMessage.getRecipient();

        if (selectedChannel != null && selectedChannel.getUuid().equals(recipientUuid)) return;

        context.dataManager().getChannels().stream()
                .filter(c -> c.getUuid().equals(recipientUuid))
                .findFirst()
                .ifPresent(graphicController::incrementUnread);
    }

    /**
     * Notification : message supprimé.
     *
     * @param deletedMessage message supprimé
     */
    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        handleNotifyMessageDeletedLogic(deletedMessage);
    }

    /**
     * Logique interne pour la suppression d'un message.
     *
     * @param deletedMessage message supprimé
     */
    private void handleNotifyMessageDeletedLogic(Message deletedMessage) {
        if (deletedMessage != null && context.logger() != null) context.logger().debug("notifyMessageDeleted: " + deletedMessage);
    }

    /**
     * Notification : message modifié.
     *
     * @param modifiedMessage message modifié
     */
    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        handleNotifyMessageModifiedLogic(modifiedMessage);
    }

    /**
     * Logique interne pour la modification d'un message.
     *
     * @param modifiedMessage message modifié
     */
    private void handleNotifyMessageModifiedLogic(Message modifiedMessage) {
        if (modifiedMessage != null && context.logger() != null) context.logger().debug("notifyMessageModified: " + modifiedMessage);
    }
}
