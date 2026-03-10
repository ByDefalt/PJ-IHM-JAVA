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

public class ListCanalController implements IListCanalController, IChannelDatabaseObserver, IUserDatabaseObserver, IMessageDatabaseObserver {

    private final ControllerContext context;
    private final IListCanalGraphicController graphicController;

    public ListCanalController(ControllerContext context, IListCanalGraphicController graphicController) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        this.context.dataManager().addObserver((IChannelDatabaseObserver) this);
        this.context.dataManager().addObserver((IUserDatabaseObserver) this);
        this.context.dataManager().addObserver((IMessageDatabaseObserver) this);

        refreshFormUsers();
    }

    private void refreshFormUsers() {
        List<User> usersWithoutMe = getUsersWithoutMe();
        this.graphicController.setupNewChannelForm(usersWithoutMe, this::createNewChannel);
    }

    private List<User> getUsersWithoutMe() {
        Set<User> all = context.dataManager().getUsers();
        User me = context.session().getConnectedUser();
        List<User> result = new ArrayList<>();
        for (User u : all) {
            if (!u.equals(me) && !u.getUuid().equals(Constants.UNKNONWN_USER_UUID)) result.add(u);
        }
        return result;
    }

    @Override
    public IListCanalGraphicController getGraphicController() {
        return graphicController;
    }

    private void setSelected(Channel channel) {
        if (context.logger() != null) context.logger().debug("Canal sélectionné : " + channel);
        context.selected().setSelectedChannel(channel);
        // Effacer le badge du canal sélectionné
        graphicController.clearUnread(channel);
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        handleNotifyChannelAddedLogic(addedChannel);
    }

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

    private boolean shouldIgnorePrivateChannel(Channel channel, User me) {
        if (channel == null) return true;
        return channel.isPrivate()
                && (me == null || (!channel.getUsers().contains(me) && !channel.getCreator().equals(me)));
    }

    private boolean isOwnerOfChannel(Channel channel, User me) {
        return channel != null && channel.isPrivate() && Objects.equals(channel.getCreator(), me);
    }

    /**
     * Construit le callback d'édition pour un canal privé.
     * Le propriétaire peut supprimer, ajouter ou retirer des membres.
     * Un membre simple peut seulement quitter.
     */
    private ChannelEditCallback buildEditCallback(Channel channel, User me, boolean isOwner) {
        // utilise channel/me/isOwner dans des logs pour éviter les warnings "paramètre non utilisé"
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

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        handleNotifyChannelDeletedLogic(deletedChannel);
    }

    private void handleNotifyChannelDeletedLogic(Channel deletedChannel) {
        if (context.logger() != null) context.logger().debug("Canal supprimé : " + deletedChannel);
        this.graphicController.removeCanal(deletedChannel);
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        handleNotifyChannelModifiedLogic(modifiedChannel);
    }

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

    @Override
    public void notifyUserAdded(User addedUser) { handleNotifyUserAddedLogic(addedUser); }

    private void handleNotifyUserAddedLogic(User addedUser) { if (context.logger() != null) { context.logger().debug("notifyUserAdded: " + addedUser); } refreshFormUsers(); }

    @Override
    public void notifyUserDeleted(User deletedUser) { handleNotifyUserDeletedLogic(deletedUser); }

    private void handleNotifyUserDeletedLogic(User deletedUser) { if (context.logger() != null) { context.logger().debug("notifyUserDeleted: " + deletedUser); } refreshFormUsers(); }

    @Override
    public void notifyUserModified(User modifiedUser) { handleNotifyUserModifiedLogic(modifiedUser); }

    private void handleNotifyUserModifiedLogic(User modifiedUser) { if (context.logger() != null) { context.logger().debug("notifyUserModified: " + modifiedUser); } refreshFormUsers(); }

    public void createNewChannel(String channelName, boolean isPrivate, List<User> invitedUsers) {
        handleCreateNewChannelLogic(channelName, isPrivate, invitedUsers);
    }

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

    /** Supprime définitivement le canal (réservé au créateur). */
    public void deleteChannel(Channel channel) {
        handleDeleteChannelLogic(channel);
    }

    private void handleDeleteChannelLogic(Channel channel) {
        if (channel == null) return;
        context.dataManager().deleteChannelFile(channel);
        if (context.logger() != null)
            context.logger().debug("Canal supprimé par son créateur : " + channel.getName());
    }

    /**
     * Retire l'utilisateur connecté du canal et persiste le canal mis à jour.
     */
    public void leaveChannel(Channel channel) {
        handleLeaveChannelLogic(channel);
    }

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

    /** Ajoute un utilisateur au canal (propriétaire uniquement). */
    public void addUserToChannel(Channel channel, User user) {
        handleAddUserToChannelLogic(channel, user);
    }

    private void handleAddUserToChannelLogic(Channel channel, User user) {
        if (channel == null || user == null) return;
        List<User> newMembers = new ArrayList<>(channel.getUsers());
        if (newMembers.contains(user)) return; // déjà membre, rien à faire
        newMembers.add(user);
        Channel updated = new Channel(channel.getUuid(), channel.getCreator(), channel.getName(), newMembers, true);
        // Mise à jour immédiate de la vue (sans attendre le watcher)
        graphicController.updateCanal(updated);
        // Persistance → déclenchera notifyChannelModified mais updateCanal sera idempotent
        context.dataManager().sendChannel(updated);
        if (context.logger() != null)
            context.logger().debug("Utilisateur ajouté au canal " + channel.getName() + " : " + user.getName());
    }

    /** Retire un utilisateur du canal (propriétaire uniquement). */
    public void removeUserFromChannel(Channel channel, User user) {
        handleRemoveUserFromChannelLogic(channel, user);
    }

    private void handleRemoveUserFromChannelLogic(Channel channel, User user) {
        if (channel == null || user == null) return;
        List<User> newMembers = new ArrayList<>(channel.getUsers());
        if (!newMembers.contains(user)) return; // pas membre, rien à faire
        newMembers.remove(user);
        Channel updated = new Channel(channel.getUuid(), channel.getCreator(), channel.getName(), newMembers, true);
        // Mise à jour immédiate de la vue (sans attendre le watcher)
        graphicController.updateCanal(updated);
        // Persistance → déclenchera notifyChannelModified mais updateCanal sera idempotent
        context.dataManager().sendChannel(updated);
        if (context.logger() != null)
            context.logger().debug("Utilisateur retiré du canal " + channel.getName() + " : " + user.getName());
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        handleNotifyMessageAddedLogic(addedMessage);
    }

    private void handleNotifyMessageAddedLogic(Message addedMessage) {
        if (addedMessage == null) return;

        // Trouver le canal destinataire du message
        Channel selectedChannel = context.selected().getSelectedChannel();
        UUID recipientUuid = addedMessage.getRecipient();

        // Si le message est dans le canal actuellement sélectionné → pas de badge
        if (selectedChannel != null && selectedChannel.getUuid().equals(recipientUuid)) return;

        // Trouver le canal correspondant dans la base et incrémenter son badge
        context.dataManager().getChannels().stream()
                .filter(c -> c.getUuid().equals(recipientUuid))
                .findFirst()
                .ifPresent(graphicController::incrementUnread);
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        handleNotifyMessageDeletedLogic(deletedMessage);
    }

    private void handleNotifyMessageDeletedLogic(Message deletedMessage) {
        // no action
        if (deletedMessage != null && context.logger() != null) context.logger().debug("notifyMessageDeleted: " + deletedMessage);
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        handleNotifyMessageModifiedLogic(modifiedMessage);
    }

    private void handleNotifyMessageModifiedLogic(Message modifiedMessage) {
        // no action
        if (modifiedMessage != null && context.logger() != null) context.logger().debug("notifyMessageModified: " + modifiedMessage);
    }
}
