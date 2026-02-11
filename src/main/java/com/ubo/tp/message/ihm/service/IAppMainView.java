package com.ubo.tp.message.ihm.service;

import java.util.function.Consumer;
import java.awt.GridBagConstraints;

/**
 * Interface exposée par la vue principale pour être utilisée par le contrôleur.
 * Ne contient que les méthodes nécessaires au contrôleur.
 */
public interface IAppMainView extends View {

    /**
     * Affiche la vue principale.
     */
    void setVisibility(boolean visible);

    /**
     * Définit le callback appelé quand un répertoire d'échange est sélectionné.
     * @param onExchangeDirectorySelected callback acceptant le chemin du répertoire
     */
    void setOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected);

    /**
     * Définit le contenu principal de la fenêtre (zone centrale).
     * Permet d'injecter des panels tels que la `LoginView`.
     * @param component composant Swing à afficher
     */
    void setMainContent(View component);

    /**
     * Ajoute une vue identifiée par un id (utilise un CardLayout en interne).
     * @param id identifiant de la vue
     * @param view composant Swing à ajouter
     */
    void addView(String id, View view);


    /**
     * Affiche la vue précédemment ajoutée identifiée par `id`.
     * @param id identifiant de la vue
     */
    void showView(String id);

    /**
     * Retire une vue précédemment ajoutée identifiée par `id`.
     * @param id identifiant de la vue
     */
    void removeView(String id);
}
