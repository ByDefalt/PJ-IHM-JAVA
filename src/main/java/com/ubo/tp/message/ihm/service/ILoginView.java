package com.ubo.tp.message.ihm.service;

public interface ILoginView extends View {
    String getTag();

    String getName();

    String getPassword();

    void setOnLoginRequested(Runnable handler);

    void setOnRegisterRequested(Runnable handler);
}

