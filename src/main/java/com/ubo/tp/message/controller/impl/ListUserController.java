package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IListUserController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.service.IListUserView;
import com.ubo.tp.message.logger.Logger;

public class ListUserController implements IListUserController {

    private final Logger logger;
    private final IDataManager dataManager;
    private final IListUserView listUserView;

    public ListUserController(Logger logger, IDataManager dataManager, IListUserView listUserView) {
        this.logger = logger;
        this.dataManager = dataManager;
        this.listUserView = listUserView;
    }

    @Override
    public IListUserView getView() {
        return listUserView;
    }
}
