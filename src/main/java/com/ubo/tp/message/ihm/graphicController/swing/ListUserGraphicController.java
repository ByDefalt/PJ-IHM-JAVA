package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphicController.service.IListUserGraphicController;
import com.ubo.tp.message.ihm.view.swing.ListUserView;
import com.ubo.tp.message.logger.Logger;

public class ListUserGraphicController implements IListUserGraphicController {

    private final Logger LOGGER;
    private final ListUserView listUserView;

    public ListUserGraphicController(Logger logger, ListUserView listUserView) {
        LOGGER = logger;
        this.listUserView = listUserView;
    }

    @Override
    public void addUser(User user) {
        if (listUserView != null) listUserView.addUser(user);
    }

    @Override
    public void removeUser(User user) {
        if (listUserView != null) listUserView.removeUser(user);
    }

    @Override
    public void updateUser(User user) {
        if (listUserView != null) listUserView.updateUser(user);
    }
}
