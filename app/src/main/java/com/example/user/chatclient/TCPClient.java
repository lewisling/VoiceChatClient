package com.example.user.chatclient;

import android.util.Log;

import java.io.*;
import android.os.Looper;
import java.net.URL;

import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;

public class TCPClient extends Thread{
    enum SERVER_ANSWER_e{NOT_RESPONDING,SERVER_FAILED,SERVER_SUCCESS};
    enum SERVER_COMMAND_e{NOTHING,START_COMM,STOP_COMM,CHECK_LOGIN,INSERT,DELETE,SEND_MESSAGE,FORGET_PASSWORD,GET_ONLINE_USERS,GUEST};

    //---------------------------Communication Variables---------------------------------//
    private String serverMessage;
    //static  String SERVERIP = "sceproject.chickenkiller.com";
    static String SERVERIP = "79.180.133.48";
    static final int SERVERPORT = 13000;
    Socket client;
    DataOutputStream out;
    OutputStream outToServer;
    InputStream inFromServer;
    ObjectInputStream objFromServer;
    DataInputStream in;
    public SERVER_COMMAND_e serverCommand;
    public String msgToServer;
    //-----------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------//
    public TCPClient() {
        client = new Socket();
        serverCommand = SERVER_COMMAND_e.NOTHING;
        StartComm();
    }
    //-----------------------------------------------------------------------------------//
    public void run() {
        try {
            switch (serverCommand)
            {
                case GET_ONLINE_USERS:
                    GetOnlineUsers();
                    break;
                case SEND_MESSAGE:
                    break;
                case START_COMM:
                    StartComm();
                    break;
                case GUEST:
                    CheckLogin();
                    break;
                case CHECK_LOGIN:
                    CheckLogin();
                    break;
                case STOP_COMM:
                    DisconnectFromServer();
                    Log.e("Stop Comm","Stop comm success");
                    return;
                case FORGET_PASSWORD:
                    ForgetPassword();
                case INSERT:
                    InsertUser();
                    break;
                case DELETE:
                    break;
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
            MainActivity.serverStatus = MainActivity.SERVER_ANSWER_e.NOT_RESPONDING;
          }
    }




    //-----------------------------------------------------------------------------------//
    //-------------------------------Communication Methods-------------------------------//
    //-----------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------//

    public void DisconnectFromServer()
    {
        if(SendMessage(msgToServer))
        {
            StopComm();
        }

    }

    private void StartComm()
    {
        Log.e("TCP Client", "C: Connecting... To" + SERVERIP + ":" + SERVERPORT);
        System.out.println("Connecting to " + SERVERIP
                + " on port " + SERVERPORT);
        try {
            client.connect(new InetSocketAddress(SERVERIP, SERVERPORT), 5000);
            System.out.println("Just connected to "
                    + client.getRemoteSocketAddress());
            outToServer = client.getOutputStream();
            out = new DataOutputStream(outToServer);
            inFromServer = client.getInputStream();

            in = new DataInputStream(inFromServer);
        }
        catch (Exception e)
        {
            MainActivity.serverStatus = MainActivity.SERVER_ANSWER_e.NOT_RESPONDING;
            Log.e("TCP", "C: Error", e);
        }
    }
    //-----------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------//
    /**
     * Disconnect from the server and close the socket.
     *      */
    public void StopComm()
    {
        try {
            if(client.isConnected() && client!=null) {
                client.close();
                Log.e("StopComm", "Stop the comm with the server success ");
            }

        } catch (Exception e) {
            Log.e("StopComm", "Error ", e);
        }
    }
    //-----------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------//
    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public boolean SendMessage(String message){
        if(client.isConnected()) {
            try {
                if (out != null) {
                    Log.e("Send Messages: ",message);
                    out.writeUTF(message);
                    return true;
                }
            } catch (Exception e) {
                Log.e("TCP", "C: Error", e);
                return false;
            }
        }
        return false;
    }

    //-----------------------------------------------------------------------------------//
    /**
     * Check the user login by email,password.
     */
    private void CheckLogin()
    {
        if(client.isConnected()) {
            try {
                //SendMessage("evyatar98@gmail.com 12345678");
                SendMessage(msgToServer);

                DataInputStream in =
                        new DataInputStream(inFromServer);

                String msg = in.readUTF().toString();
                if (msg.contains("Success")) {
                    // StopComm();
                    MainActivity.serverStatus = MainActivity.SERVER_ANSWER_e.SERVER_SUCCESS;
                    return;
                }

                MainActivity.serverStatus = MainActivity.SERVER_ANSWER_e.SERVER_FAILED;
                StopComm();

                return;

            } catch (IOException e) {
                e.printStackTrace();
                MainActivity.serverStatus = MainActivity.SERVER_ANSWER_e.SERVER_FAILED;
                StopComm();
            }
        }
    }
    private boolean InsertUser()
    {
        if(client.isConnected()) {
            try {
                //SendMessage("evyatar98@gmail.com 12345678");
                SendMessage(msgToServer);

                DataInputStream in =
                        new DataInputStream(inFromServer);

                String msg = in.readUTF().toString();
                if (msg.contains("Success")) {
                    StopComm();
                    MainActivity.serverStatus = MainActivity.SERVER_ANSWER_e.SERVER_SUCCESS;
                    return true;
                }

                MainActivity.serverStatus = MainActivity.SERVER_ANSWER_e.SERVER_FAILED;
                StopComm();

                return false;

            } catch (IOException e) {
                e.printStackTrace();
                MainActivity.serverStatus = MainActivity.SERVER_ANSWER_e.SERVER_FAILED;
                StopComm();
                return false;
            }
        }
        return false;
    }
    //-----------------------------------------------------------------------------------//
    private boolean ForgetPassword()
    {
        if(client.isConnected()) {
            try {
                SendMessage(msgToServer);

                DataInputStream in =
                        new DataInputStream(inFromServer);

                String msg = in.readUTF().toString();
                if (msg.contains("Success")) {
                    StopComm();
                    MainActivity.serverStatus = MainActivity.SERVER_ANSWER_e.SERVER_SUCCESS;
                    return true;
                }

                MainActivity.serverStatus = MainActivity.SERVER_ANSWER_e.SERVER_FAILED;
                StopComm();

                return false;

            } catch (IOException e) {
                e.printStackTrace();
                MainActivity.serverStatus = MainActivity.SERVER_ANSWER_e.SERVER_FAILED;
                StopComm();
                return false;
            }
        }
        return false;

    }
    //------------------------------------------------------------------------------------//
    public OnlineUsers GetOnlineUsers() {

        OnlineUsers loginUsers = null;
        if(client.isConnected()) {
            try {

                SendMessage(msgToServer);
                //Log.e("GetOnlineUsers", "Before create ObjectInputStream");

                ObjectInputStream objFromServer1 = new ObjectInputStream(inFromServer);

                //Log.e("GetOnlineUsers", "After create ObjectInputStream");

                try {
                    ReceiveMessages.receiveMessage = false;
                    Log.e("GetOnlineUsers","Start");
                    Object o = objFromServer1.readObject();
                    ReceiveMessages.receiveMessage = true;
                    if(o instanceof OnlineUsers)
                    {
                        loginUsers = (OnlineUsers)o;
                        Log.e("Get OnlineUsers Success", loginUsers.getClass().toString());
                    }
                    else
                        Log.e("GetOnlineUsers", "Get OnlineUsers Failed");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    MainChatActivity.serverStatus = MainChatActivity.SERVER_ANSWER_e.SERVER_FAILED;

                    return null;
                }

                MainChatActivity.serverStatus = MainChatActivity.SERVER_ANSWER_e.SERVER_SUCCESS;
                return loginUsers;


            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error: ", e.getMessage());
                //MainChatActivity.serverStatus = MainChatActivity.SERVER_ANSWER_e.SERVER_FAILED;
                //StopComm();

                return null;
            }
        }
        return null;
    }
    //------------------------------------------------------------------------------------//
    public String GetChatMessage()
    {
        String msg = "";

        if(client.isConnected())
        {
            try
            {
                DataInputStream in =
                        new DataInputStream(inFromServer);

                msg = in.readUTF().toString();

                if(msg.contains("GET_MESSAGE"))
                    return msg;
                    //msg =  msg.split(",")[3];//GET_MESSAGE,"+emailUser+","+emailSend+","+translateMsg
            }

            catch (IOException e)
            {
                e.printStackTrace();
                return msg;
            }
         }
        return "";
    }
    public void updateUser(String MesToServer)
    {
        try {
            if(this.SendMessage(MesToServer))
                settingActivity.serverStatus=settingActivity.SERVER_ANSWER_e.SERVER_SUCCESS;
            else settingActivity.serverStatus=settingActivity.SERVER_ANSWER_e.NOT_RESPONDING;
        }catch (Exception e)
        {
            settingActivity.serverStatus=settingActivity.SERVER_ANSWER_e.SERVER_FAILED;
        }
    }
    private void getIpFromServer() {
        try {
            URL url = new URL("http://transapp.adirapps.com/adirapps/uploads/2015/06/serverip.ini");
            InputStream is = url.openStream();
            int buff = is.read() , counterByte = 0;
            String str = "";
            while (buff != -1 || counterByte > 50)
            {
                str = str + (char)buff;
                buff = is.read();
                counterByte++;
            }
            if(str.length() > 15 || str.length() < 11)
                throw new EmptyStackException();
            SERVERIP = str;
            Log.e("TCP Client", "get ip from server success :" + str ) ;
        }
        catch (Exception e)
        {
            Log.e("TCP Client", "Error!! in get ip from server :"+e.toString());
        }

    }
    //------------------------------------------------------------------------------------//
    //-----------------------------End Of Communication Methods-----------==-------------//
    //-----------------------------------------------------------------------------------//
}

