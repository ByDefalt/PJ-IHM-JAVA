package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IListCanalController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.service.IListCanalView;
import com.ubo.tp.message.logger.Logger;

public class ListCanalController implements IListCanalController {

    private final Logger LOGGER;
    private final IDataManager dataManager;
    private final IListCanalView view;

    public ListCanalController(Logger logger, IDataManager dataManager, IListCanalView view) {
        LOGGER = logger;
        this.dataManager = dataManager;
        this.view = view;
    }

    @Override
    public IListCanalView getView() {
        return view;
    }
}
