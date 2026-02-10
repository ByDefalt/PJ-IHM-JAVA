package com.ubo.tp.message.ihm.initializer.registry;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;

import javax.swing.JComponent;
import java.util.Set;
import java.util.function.Function;

/**
 * Registry pour enregistrer des créateurs de vues identifiés par un id.
 */
public interface ViewRegistry {
    void register(String id, Function<InitializationContext, JComponent> creator);
    boolean has(String id);
    JComponent create(String id, InitializationContext context);
    Set<String> getIds();
    void remove(String id);
}
