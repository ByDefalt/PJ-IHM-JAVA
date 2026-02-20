package com.ubo.tp.message.theme;

/**
 * Abstraction d'un thème applicatif.
 * Implementations must apply a Look & Feel and UI defaults.
 */
public abstract class AbstractTheme {

    /**
     * Apply the theme (install LAF, UI defaults, etc.)
     */
    public abstract void apply();
}

