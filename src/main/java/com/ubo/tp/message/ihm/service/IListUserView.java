package com.ubo.tp.message.ihm.service;

import java.util.List;

public interface IListUserView extends View{
    void setUsers(List<IUserView> newUsers);
    void addUser(IUserView user);
    void clearUsers();
}

