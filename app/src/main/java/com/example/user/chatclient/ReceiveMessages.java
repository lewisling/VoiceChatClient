package com.example.user.chatclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by user on 25/05/2015.
 */
public class ReceiveMessages implements Runnable{
    Thread mTcpClient;
    static boolean receiveMessage;

    public ReceiveMessages(Thread receiverThread)
    {
        this.mTcpClient = receiverThread;
        receiveMessage = true;



    }

    @Override
    public void run()
    {
        while(true) {
            if(receiveMessage) {
                Log.e("GetMessage(run", "Trying to get a new message");
                MainChatActivity.keepUpdateOnlineUsers = false;
                String msgChat = ((TCPClient) (MainActivity.mTcpClient)).GetChatMessage();
                MainChatActivity.keepUpdateOnlineUsers = true;

                if (msgChat != null && !msgChat.isEmpty()) {
                    System.out.println(msgChat);
                    String decMsg = EncryptUtils.base64decode(msgChat.split("`")[3]);

                    //ChatRoomActivity.msgList.add("Friend: " +decMsg );
                    if(MainActivity.historyChat.containsKey(msgChat.split("`")[1].toLowerCase()))
                    {
                        MainActivity.historyChat.get(msgChat.split("`")[1]).add(ChatRoomActivity.destEmail+"  : " + decMsg);

                        Notification();

                    }
                    else
                    {
                        MainActivity.historyChat.put(msgChat.split("`")[1],new ArrayList<String>());
                        MainActivity.historyChat.get(msgChat.split("`")[1].toLowerCase()).add(ChatRoomActivity.destEmail+"  : " +decMsg );

                        Notification();

                    }
                }
                Log.e("GetMessage(run", "Success to get a new message");
            }
        }
    }



    private void Notification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.mContext);
        builder.setAutoCancel(true);
        builder.setContentTitle("TransApp");
        builder.setContentText(ChatRoomActivity.destEmail + " send you a message!");
        Bitmap bm = BitmapFactory.decodeResource(MainActivity.mContext.getResources(), R.drawable.ic_launcher);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) MainActivity.mContext.getSystemService(MainActivity.NOTIFICATION_SERVICE);
        manager.notify(8,notification);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

    }
}
