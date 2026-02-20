package com.ubo.tp.message.ihm.service;

public interface IRegisterView extends View {
    String getTag();

    String getName();

    String getPassword();

    String getConfirmPassword();

    void setOnRegisterRequested(Runnable handler);

    void setOnBackToLoginRequested(Runnable handler);

    void clearFields();
}

