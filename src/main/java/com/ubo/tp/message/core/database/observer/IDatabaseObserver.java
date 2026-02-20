package com.ubo.tp.message.core.database.observer;

/**
 * Observateur des modifications de la base de données (interface combinée).
 * <p>
 * Cette interface regroupe les observateurs pour les messages, les
 * utilisateurs et les canaux afin de conserver la compatibilité
 * ascendante. Préférez cependant implémenter les interfaces
 * spécialisées lorsque cela est possible.
 */
public interface IDatabaseObserver extends IMessageDatabaseObserver, IUserDatabaseObserver, IChannelDatabaseObserver {

    // Interface combinée conservée pour compatibilité ascendante.
}
