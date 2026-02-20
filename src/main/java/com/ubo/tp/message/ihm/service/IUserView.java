package com.ubo.tp.message.ihm.service;

import javax.swing.*;

public interface IUserView extends View {
    String getUserName();

    void setUserName(String name);

    String getStatus();

    void setStatus(String status);

    JLabel getUserNameLabel();
}
