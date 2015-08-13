package com.example.user.chatclient;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class OnlineUsers implements Serializable{
    Map<String,User> loginUsers = new HashMap<String,User>();
    public OnlineUsers()
    {

    }
    public void AddUser(User u)
    {
        if(!loginUsers.containsKey(u.email.toLowerCase()))
        {
            loginUsers.put(u.email.toLowerCase(), u);
        }
    }
    public void DeleteUser(String email)
    {
        if(!loginUsers.containsKey(email.toLowerCase()))
        {
            loginUsers.remove(email.toLowerCase());
        }
    }
    public User GetUserByEmail(String email)
    {
        return loginUsers.containsKey(email.toLowerCase())?
                loginUsers.get(email.toLowerCase()):
                null;
    }

    public String GetLangByEmail(String email)
    {
        return loginUsers.containsKey(email.toLowerCase())?
                loginUsers.get(email.toLowerCase()).lang:
                null;
    }

    public Map<String,User> GetOnlineUsers()
    {
        return loginUsers;
    }
    /**
     * Users that the user spoke with
     * @param emailUser
     * @param emailChat
     */
    public void AddUserToChatList(String emailUser,String emailChat)
    {
        if(loginUsers.containsKey(emailUser.toLowerCase()))
        {
            if(!loginUsers.get(emailUser).chatWith.contains(emailChat))
                loginUsers.get(emailUser).chatWith.add(emailChat);
        }
    }
    public void DeleteUserFromChatWithList(String emailUser,String emailChat)
    {
        if(loginUsers.containsKey(emailUser.toLowerCase()))
        {
            if(loginUsers.get(emailUser).chatWith.contains(emailChat))
                loginUsers.get(emailUser).chatWith.remove(emailChat);

        }
    }

}
