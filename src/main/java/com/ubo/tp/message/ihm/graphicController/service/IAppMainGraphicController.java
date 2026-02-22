package com.ubo.tp.message.ihm.graphicController.service;

import com.ubo.tp.message.ihm.view.service.View;

import java.util.function.Consumer;

public interface IAppMainGraphicController extends GraphicController {
    void setVisibility(boolean visible);

    void setOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected);

    void setMainView(View component);

    void setClearSelected(Runnable clearSelected);

    void setOnDisconnect(Runnable onDisconnect);

    void setOnDeleteAccount(Runnable onDeleteAccount);
}
