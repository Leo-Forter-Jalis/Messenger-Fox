package com.lfj.messenger.base.cache;

import com.lfj.dev.annotations.NotNull;
import com.lfj.dev.annotations.Nullable;
import com.lfj.messenger.base.db.UserService;
import com.lfj.messenger.dto.datatype.UserDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class UserCache {
    private final Map<UUID, UserDTO> userCache = new ConcurrentHashMap<>();
    private UserService service;
    private UserCache(){  }
    public UserCache(@NotNull UserService service) {
        this.service = service;
    }
    public void putUser(@NotNull UserDTO userDTO){
        this.userCache.put(userDTO.userId(), userDTO);
        this.service.addUser(userDTO);
    }
    public void removeUser(@NotNull UUID id){
        this.userCache.remove(id);
        this.service.removeUser(id);
    }
    public @Nullable Optional<UserDTO> getUser(@NotNull UUID id){
        return Optional.ofNullable(this.userCache.get(id));
    }
    public Stream<Map.Entry<UUID, UserDTO>> entrySetStream() {
        return userCache.entrySet().stream();
    }
    public Stream<UserDTO> valuesStream(){
        return userCache.values().stream();
    }
    public Stream<UUID> keySetStream(){
        return userCache.keySet().stream();
    }
    private void loadUserCache(){
        List<UserDTO> list = this.service.selectAllUser().orElse(List.of());
        if(list.isEmpty()) return;
        for(UserDTO user : list){
            this.userCache.put(user.userId(), user);
        }
    }

}
