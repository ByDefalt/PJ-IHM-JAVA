package com.ubo.tp.message.ihm.initializer.registry;

import com.ubo.tp.message.ihm.initializer.model.InitializationContext;

import javax.swing.JComponent;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DefaultViewRegistry implements ViewRegistry {

    private final Map<String, Function<InitializationContext, JComponent>> creators = new ConcurrentHashMap<>();

    @Override
    public void register(String id, Function<InitializationContext, JComponent> creator) {
        if (id == null || creator == null) return;
        creators.putIfAbsent(id, creator);
    }

    @Override
    public boolean has(String id) {
        return creators.containsKey(id);
    }

    @Override
    public JComponent create(String id, InitializationContext context) {
        Function<InitializationContext, JComponent> f = creators.get(id);
        return f != null ? f.apply(context) : null;
    }

    @Override
    public Set<String> getIds() {
        return creators.keySet();
    }

    @Override
    public void remove(String id) {
        creators.remove(id);
    }
}
