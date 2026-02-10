package com.ubo.tp.message.navigation;

import com.ubo.tp.message.ihm.service.IMessageAppMainView;

import javax.swing.JComponent;
import java.awt.GridBagConstraints;
import java.util.HashSet;
import java.util.Set;

/**
 * Implémentation de {@link NavigationService} basée sur la vue principale
 * ({@link IMessageAppMainView}).
 * <p>
 * Gère un ensemble d'identifiants enregistrés et délègue l'ajout/affichage
 * à la vue principale. Les opérations d'enregistrement sont idempotentes.
 * </p>
 */
public class AppNavigationService implements NavigationService {

    private final IMessageAppMainView mainView;
    private final Set<String> registered = new HashSet<>();

    /**
     * Crée un service de navigation attaché à la vue principale.
     *
     * @param mainView vue principale utilisée pour l'affichage des composants
     */
    public AppNavigationService(IMessageAppMainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void addView(String id, JComponent view) {
        if (id == null || view == null) return;
        if (registered.contains(id)) return; // idempotent
        mainView.addView(id, view);
        registered.add(id);
    }

    @Override
    public void addView(String id, JComponent view, GridBagConstraints gbc) {
        if (id == null || view == null) return;
        if (registered.contains(id)) return; // idempotent
        mainView.addView(id, view, gbc);
        registered.add(id);
    }

    @Override
    public void showView(String id) {
        mainView.showView(id);
    }

    @Override
    public boolean hasView(String id) {
        return registered.contains(id);
    }

    @Override
    public void removeView(String id) {
        if (id == null) return;
        if (registered.remove(id)) {
            mainView.removeView(id);
        }
    }
}
