package com.ubo.tp.message.ihm.initializer.module;

import com.ubo.tp.message.controller.impl.EmptyController;
import com.ubo.tp.message.controller.service.IEmptyController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.component.SwingComponentWrapper;
import com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry;
import com.ubo.tp.message.ihm.initializer.registry.ViewRegistry;
import com.ubo.tp.message.ihm.initializer.registry.utils.ViewRegistryUtils;
import com.ubo.tp.message.ihm.screen.EmptyView;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

import javax.swing.*;

public class EmptyModule implements UIModule{
    @Override
    public void register(NavigationService navigation, IDataManager dataManager, Logger logger, ControllerRegistry controllerRegistry, ViewRegistry viewRegistry) {
        controllerRegistry.register("emptyController", IEmptyController.class,
                ctx -> new EmptyController());
        viewRegistry.register("empty", ViewRegistryUtils.createViewFromControllerSingle(
                "emptyController",
                IEmptyController.class,
                ctx -> new EmptyController(),
                ctx -> new SwingComponentWrapper(new JPanel()),
                (ctrl, comp, ctx) -> new EmptyView(ctrl, ctx.getLogger())
        ));
    }
}
