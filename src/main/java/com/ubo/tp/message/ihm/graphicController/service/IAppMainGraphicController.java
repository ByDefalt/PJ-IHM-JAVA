package com.ubo.tp.message.ihm.graphicController.service;

import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.view.swing.AppMainView;

import java.util.function.Consumer;

public interface IAppMainGraphicController extends GraphicController {
    void setVisibility(boolean visible);

    void setOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected);

    void setMainContent(View component);

    AppMainView getAppMainView();
}

