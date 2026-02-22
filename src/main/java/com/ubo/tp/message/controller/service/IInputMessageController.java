package com.ubo.tp.message.controller.service;

import java.util.UUID;

public interface IInputMessageController extends Controller {
    void sendMessage(UUID uuid, String message);
}
