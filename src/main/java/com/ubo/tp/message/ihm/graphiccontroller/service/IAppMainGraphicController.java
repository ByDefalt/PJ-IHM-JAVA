package com.ubo.tp.message.ihm.graphiccontroller.service;

import com.ubo.tp.message.ihm.view.service.View;

import java.util.function.Consumer;

public interface IAppMainGraphicController extends GraphicController {
    void setVisibility(boolean visible);

    void setOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected);

    void setMainView(View component);

    void setOnDisconnect(Runnable onDisconnect);

    void setOnDeleteAccount(Runnable onDeleteAccount);

    /**
     * Enregistre le callback à appeler quand l'utilisateur ferme l'application.
     * Le controller applicatif fournit ce callback pour gérer déconnexion + arrêt.
     */
    void setOnClose(Runnable onClose);

    /**
     * Affiche ou masque les éléments de menu liés à la session (déconnexion, profil…).
     * Appelé par le controller applicatif en réponse aux événements de session.
     */
    void setConnectMenuVisible(boolean visible);
}
