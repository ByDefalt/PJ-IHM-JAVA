package com.ubo.tp.message.ihm.service;

import javax.swing.*;

public interface ICanalView extends View {
    String getCanalName();

    void setCanalName(String name);

    JLabel getCanalLabel();
}
