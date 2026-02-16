package com.ubo.tp.message.ihm.view;

import com.ubo.tp.message.ihm.service.View;
import com.ubo.tp.message.ihm.service.IChatMainView;
import com.ubo.tp.message.ihm.service.IListCanalView;
import com.ubo.tp.message.ihm.service.IListUserView;
import com.ubo.tp.message.ihm.service.IListMessageView;
import com.ubo.tp.message.ihm.service.IInputMessageView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Vue composite principal (assemblage des vues de canaux, utilisateurs, messages et saisie).
 * Utilise exclusivement GridBagLayout pour le placement des composants.
 */
public class ChatMainView extends JComponent implements IChatMainView {

    private final Logger logger;

    private final IListCanalView listCanalView;
    private final IListUserView listUserView;
    private final IListMessageView listMessageView;
    private final IInputMessageView inputMessageView;

    public ChatMainView(Logger logger, IListCanalView listCanalView, IListUserView listUserView, IListMessageView listMessageView, IInputMessageView inputMessageView) {
        this.logger = logger;
        this.listCanalView = listCanalView;
        this.listUserView = listUserView;
        this.listMessageView = listMessageView;
        this.inputMessageView = inputMessageView;

        // Racine en GridBagLayout
        this.setLayout(new GridBagLayout());
        this.setOpaque(true);
        this.setBackground(new Color(54, 57, 63));

        // left panel: tabbed pane (Canaux / Utilisateurs)
        JTabbedPane leftTabs = new JTabbedPane();
        leftTabs.addTab("Canaux", (Component) this.listCanalView);
        leftTabs.addTab("Utilisateurs", (Component) this.listUserView);
        leftTabs.setBackground(new Color(47, 49, 54));
        leftTabs.setForeground(Color.WHITE);

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.gridwidth = 1;
        gbcLeft.gridheight = 1;
        gbcLeft.weightx = 0.28; // environ 28% largeur
        gbcLeft.weighty = 1.0;
        gbcLeft.fill = GridBagConstraints.BOTH;
        gbcLeft.anchor = GridBagConstraints.WEST;
        gbcLeft.insets = new Insets(6, 6, 6, 6);

        this.add(leftTabs, gbcLeft);

        // right panel construit également en GridBagLayout (header, messages, input)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(true);
        rightPanel.setBackground(new Color(54, 57, 63));

        // header (channel name)
        JLabel header = new JLabel("# général");
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setForeground(Color.WHITE);
        header.setOpaque(false);

        GridBagConstraints gbcHeader = new GridBagConstraints();
        gbcHeader.gridx = 0;
        gbcHeader.gridy = 0;
        gbcHeader.gridwidth = 1;
        gbcHeader.gridheight = 1;
        gbcHeader.weightx = 1.0;
        gbcHeader.weighty = 0.0;
        gbcHeader.fill = GridBagConstraints.HORIZONTAL;
        gbcHeader.anchor = GridBagConstraints.NORTHWEST;
        gbcHeader.insets = new Insets(6, 6, 6, 6);

        rightPanel.add(header, gbcHeader);

        // message list center
        GridBagConstraints gbcMessages = new GridBagConstraints();
        gbcMessages.gridx = 0;
        gbcMessages.gridy = 1;
        gbcMessages.gridwidth = 1;
        gbcMessages.gridheight = 1;
        gbcMessages.weightx = 1.0;
        gbcMessages.weighty = 1.0; // prend tout l'espace vertical restant
        gbcMessages.fill = GridBagConstraints.BOTH;
        gbcMessages.anchor = GridBagConstraints.CENTER;
        gbcMessages.insets = new Insets(0, 6, 6, 6);

        rightPanel.add((Component) this.listMessageView, gbcMessages);

        // input area bottom
        GridBagConstraints gbcInput = new GridBagConstraints();
        gbcInput.gridx = 0;
        gbcInput.gridy = 2;
        gbcInput.gridwidth = 1;
        gbcInput.gridheight = 1;
        gbcInput.weightx = 1.0;
        gbcInput.weighty = 0.0;
        gbcInput.fill = GridBagConstraints.HORIZONTAL;
        gbcInput.anchor = GridBagConstraints.SOUTH;
        gbcInput.insets = new Insets(6, 6, 6, 6);

        rightPanel.add((Component) this.inputMessageView, gbcInput);

        // ajouter rightPanel à la racine
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 1;
        gbcRight.gridy = 0;
        gbcRight.gridwidth = 1;
        gbcRight.gridheight = 1;
        gbcRight.weightx = 0.72; // reste de la largeur
        gbcRight.weighty = 1.0;
        gbcRight.fill = GridBagConstraints.BOTH;
        gbcRight.anchor = GridBagConstraints.EAST;
        gbcRight.insets = new Insets(6, 0, 6, 6);

        this.add(rightPanel, gbcRight);

        if (this.logger != null) this.logger.debug("ChatMainView initialisée (GridBagLayout)");
    }

    // Getters pour que le contrôleur puisse attacher des listeners / rafraîchir
    public IListCanalView getListCanalView() { return listCanalView; }
    public IListUserView getListUserView() { return listUserView; }
    public IListMessageView getListMessageView() { return listMessageView; }
    public IInputMessageView getInputMessageView() { return inputMessageView; }

}
