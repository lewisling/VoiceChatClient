package com.example.user.chatclient;

import android.app.Activity;
import android.util.Log;

/**
 * Created by user on 24/05/2015.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        // your code here (Need to write only once for entire app)
        if(MainChatActivity.toFinish)
        {
            MainChatActivity.toFinish = false;
            ((Activity)  MainChatActivity.mContext).finish();
        }
    }
    @Override
    protected void onStop()
    {
        super.onStop();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(this instanceof MainActivity && MainActivity.hasConnected) {
            Log.e("On Destroy", "On Destory");
            if (MainActivity.userPermissionE == MainActivity.USER_PERMISSION_e.USER) {
                ((TCPClient) (MainActivity.mTcpClient)).serverCommand = TCPClient.SERVER_COMMAND_e.STOP_COMM;
                ((TCPClient) (MainActivity.mTcpClient)).msgToServer = "DISCONNECT " + MainChatActivity.userEmail;//append email
                ((TCPClient) (MainActivity.mTcpClient)).DisconnectFromServer();
                Log.e("Disconnect", "Disconnect from server");
            } else {
                ((TCPClient) (MainActivity.mTcpClient)).serverCommand = TCPClient.SERVER_COMMAND_e.STOP_COMM;
                ((TCPClient) (MainActivity.mTcpClient)).StopComm();
            }
        }

    }
}
