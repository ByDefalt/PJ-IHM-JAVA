package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.common.Constants;
import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListCanalController;
import com.ubo.tp.message.core.database.observer.IChannelDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListCanalGraphicController;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListCanalGraphicController.ChannelEditCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ListCanalController implements IListCanalController, IChannelDatabaseObserver, IUserDatabaseObserver {

    private final ControllerContext context;
    private final IListCanalGraphicController graphicController;

    public ListCanalController(ControllerContext context, IListCanalGraphicController graphicController) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        this.context.dataManager().addObserver((IChannelDatabaseObserver) this);
        this.context.dataManager().addObserver((IUserDatabaseObserver) this);

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
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        if (context.logger() != null) context.logger().debug("Canal ajouté : " + addedChannel);
        User me = context.session().getConnectedUser();

        if (addedChannel.isPrivate()
                && !addedChannel.getUsers().contains(me)
                && !addedChannel.getCreator().equals(me)) {
            if (context.logger() != null)
                context.logger().debug("Ignorer le canal privé qui ne m'inclut pas : " + addedChannel);
            return;
        }

        boolean isOwner = addedChannel.isPrivate() && addedChannel.getCreator().equals(me);
        ChannelEditCallback onEdit = addedChannel.isPrivate() ? buildEditCallback(addedChannel, me, isOwner) : null;

        this.graphicController.addCanal(addedChannel, this::setSelected, onEdit, isOwner, this::getUsersWithoutMe);
    }

    /**
     * Construit le callback d'édition pour un canal privé.
     * Le propriétaire peut supprimer, ajouter ou retirer des membres.
     * Un membre simple peut seulement quitter.
     */
    private ChannelEditCallback buildEditCallback(Channel channel, User me, boolean isOwner) {
        return new ChannelEditCallback() {
            @Override
            public void onLeave(Channel c) { leaveChannel(c); }

            @Override
            public void onDelete(Channel c) { deleteChannel(c); }

            @Override
            public void onAddUser(Channel c, User user) { addUserToChannel(c, user); }

            @Override
            public void onRemoveUser(Channel c, User user) { removeUserFromChannel(c, user); }
        };
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        if (context.logger() != null) context.logger().debug("Canal supprimé : " + deletedChannel);
        this.graphicController.removeCanal(deletedChannel);
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        if (context.logger() != null) context.logger().debug("Canal modifié : " + modifiedChannel);
        User me = context.session().getConnectedUser();

        if (modifiedChannel.isPrivate()
                && !modifiedChannel.getCreator().equals(me)
                && !modifiedChannel.getUsers().contains(me)) {
            this.graphicController.removeCanal(modifiedChannel);
        } else {
            boolean isOwner = modifiedChannel.isPrivate() && modifiedChannel.getCreator().equals(me);
            IListCanalGraphicController.ChannelEditCallback onEdit = modifiedChannel.isPrivate() ?
                    buildEditCallback(modifiedChannel, me, isOwner) : null;
            this.graphicController.addCanal(modifiedChannel, this::setSelected, onEdit, isOwner, this::getUsersWithoutMe);

            this.graphicController.updateCanal(modifiedChannel);
        }
    }

    @Override
    public void notifyUserAdded(User addedUser) { refreshFormUsers(); }

    @Override
    public void notifyUserDeleted(User deletedUser) { refreshFormUsers(); }

    @Override
    public void notifyUserModified(User modifiedUser) { refreshFormUsers(); }

    public void createNewChannel(String channelName, boolean isPrivate, List<User> invitedUsers) {
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
        if (channel == null) return;
        context.dataManager().deleteChannelFile(channel);
        if (context.logger() != null)
            context.logger().debug("Canal supprimé par son créateur : " + channel.getName());
    }

    /**
     * Retire l'utilisateur connecté du canal et persiste le canal mis à jour.
     */
    public void leaveChannel(Channel channel) {
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
}
