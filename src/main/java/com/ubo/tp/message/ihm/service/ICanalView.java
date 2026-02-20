package com.ubo.tp.message.ihm.service;

import com.ubo.tp.message.datamodel.Channel;

public interface ICanalView extends View {
    Channel getChannel();

    void updateChannel(Channel channel);
}
