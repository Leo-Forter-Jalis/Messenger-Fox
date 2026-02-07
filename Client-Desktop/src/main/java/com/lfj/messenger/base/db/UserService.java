package com.lfj.messenger.base.db;

import com.lfj.messenger.dto.datatype.UserDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private UserDB userDB;
    public UserService(UserDB userDB){
        this.userDB = userDB;
    }
    public void addUser(UserDTO userDTO){
        this.userDB.insertUserAsync(userDTO);
    }
    public Optional<UserDTO> selectUser(UUID id){
        return this.userDB.selectUserAsync(id).join();
    }
    public Optional<List<UserDTO>> selectAllUser(){
        return this.userDB.selectAllUserAsync().join();
    }
    public void removeUser(UUID id){
        this.userDB.deleteUserAsync(id);
    }
}
