package com.lfj.messenger.server.registry;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionRegistry {
    private final Map<UUID, Channel> connections = new ConcurrentHashMap<>();

    public void add(UUID uuid, Channel channel) { this.connections.put(uuid, channel); }
    public void remove (UUID uuid) { this.connections.remove(uuid); }
    public Optional<Channel> get(UUID uuid) { return  Optional.ofNullable(this.connections.get(uuid)); }
    public boolean isOnline(UUID uuid){ return this.connections.containsKey(uuid); }
}
