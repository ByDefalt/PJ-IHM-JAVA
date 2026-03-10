package com.ubo.tp.message.ihm.graphiccontroller.service;

import com.ubo.tp.message.ihm.view.service.View;

import java.util.function.Consumer;

/**
 * Contrat graphique pour le contrôleur principal de l'application (fenêtre principale).
 *
 * Fournit des méthodes pour contrôler la visibilité de la fenêtre, gérer
 * les callbacks de navigation et l'affichage du contenu principal.
 */
public interface IAppMainGraphicController extends GraphicController {

    /**
     * Affiche ou masque la fenêtre principale.
     *
     * @param visible true pour afficher, false pour masquer
     */
    void setVisibility(boolean visible);

    /**
     * Définit le callback appelé quand l'utilisateur sélectionne un répertoire d'échange.
     *
     * @param onExchangeDirectorySelected callback recevant le chemin sélectionné
     */
    void setOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected);

    /**
     * Définit la vue principale à afficher dans la zone centrale de l'application.
     *
     * @param component composant à afficher
     */
    void setMainView(View component);

    /**
     * Définit le callback appelé lors d'une déconnexion demandée depuis l'UI.
     *
     * @param onDisconnect action à exécuter pour déconnecter l'utilisateur
     */
    void setOnDisconnect(Runnable onDisconnect);

    /**
     * Définit le callback appelé lors de la suppression du compte depuis l'UI.
     *
     * @param onDeleteAccount action à exécuter pour supprimer le compte
     */
    void setOnDeleteAccount(Runnable onDeleteAccount);

    /**
     * Enregistre le callback à appeler quand l'utilisateur ferme l'application.
     * Le controller applicatif fournit ce callback pour gérer déconnexion + arrêt.
     *
     * @param onClose callback exécuté lorsque l'application est fermée
     */
    void setOnClose(Runnable onClose);

    /**
     * Affiche ou masque les éléments de menu liés à la session (déconnexion, profil…).
     * Appelé par le controller applicatif en réponse aux événements de session.
     *
     * @param visible true pour montrer, false pour masquer
     */
    void setConnectMenuVisible(boolean visible);
}
