package com.example.user.chatclient;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Handler;
import android.app.AlertDialog;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Map;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import static com.example.user.chatclient.R.layout.activity_main_chat;

public class MainChatActivity extends BaseActivity {
    enum SERVER_ANSWER_e{NOT_RESPONDING,SERVER_FAILED,SERVER_SUCCESS};
    static SERVER_ANSWER_e serverStatus;
    private ListView onlineList;
    static boolean toFinish;
    static Context mContext;
    static Map<String, User> onlineUsersList;
    static String userEmail;
    boolean EditButtonImage=false;
    Boolean hasOnlineUsers;
    static Boolean keepUpdateOnlineUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        Bundle b = getIntent().getExtras();
        userEmail= b.getString("sourceEmail");
        mContext = this;
        keepUpdateOnlineUsers = true;
        AddButtonListeners();
        initControls();
        final Handler mHandler = new Handler();

        toFinish = false;

        // Create runnable for posting
        final Runnable mUpdateResults = new Runnable() {
            public void run() {
                if(keepUpdateOnlineUsers)
                    UpdateOnlineUsersList();
            }
        };
        int delay = 1000; // delay for 1 sec.
        int period = 4000; // repeat every 4 sec.

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                mHandler.post(mUpdateResults);

            }

        }, delay, period);

    }

        public void  initControls() {
        onlineList = (ListView) findViewById(R.id.listOnlineUsers);
        onlineList.setTextFilterEnabled(true);

        onlineList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
               if(hasOnlineUsers && MainActivity.userPermissionE != MainActivity.USER_PERMISSION_e.GUEST) {
                   String selectItem = onlineList.getItemAtPosition(position).toString();//email (nickname)
                   String destEmail = onlineUsersList.get(selectItem.split(" ")[0]).email;//take only the email
                   Intent i = new Intent(MainChatActivity.this, ChatRoomActivity.class);
                   i.putExtra("destEmail", destEmail);
                   i.putExtra("sourceEmail", userEmail);

                   startActivity(i);
               }
            }
        });

    }

    public void AddButtonListeners()
    {
        final Button EditBtn = (Button)findViewById(R.id.editbutton);
        final Button LogOutBtn = (Button)findViewById(R.id.logoutBtn);
        ImageView TAppIMG = (ImageView)findViewById(R.id.icon_image);
        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p= (onlineUsersList.get(userEmail)).Permission;
                if ((onlineUsersList.get(userEmail)).Permission.equals("1")) {
                    Intent edit = new Intent(MainChatActivity.this, settingActivity.class);
                    edit.putExtra("User", userEmail);
                    edit.putExtra("perr", "1");
                    startActivity(edit);
                } else {
                    Intent edit = new Intent(MainChatActivity.this, updateUsersAdmin.class);
                    startActivity(edit);
                }
            }
        });
        TAppIMG.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(EditButtonImage==false)
                {
                    EditBtn.setVisibility(View.VISIBLE);
                    LogOutBtn.setVisibility(View.VISIBLE);
                    EditButtonImage=true;


                }
                else
                {
                    EditBtn.setVisibility(View.GONE);
                    LogOutBtn.setVisibility(View.GONE);
                    EditButtonImage=false;
                }
                return false;
            }
        });
        LogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TCPClient) (MainActivity.mTcpClient)).serverCommand = TCPClient.SERVER_COMMAND_e.STOP_COMM;
                ((TCPClient) (MainActivity.mTcpClient)).msgToServer = "DISCONNECT " + userEmail ;//append email
                ((TCPClient) (MainActivity.mTcpClient)).DisconnectFromServer();
                Log.e("Disconnect", "Disconnect from server");
                finish();

            }
        });
    }


    public void UpdateOnlineUsersList()
    {
                OnlineUsers onlineUsersObject = null;
                ListView listview = (ListView) findViewById(R.id.listOnlineUsers);

                try {

                    ((TCPClient) (MainActivity.mTcpClient)).serverCommand = TCPClient.SERVER_COMMAND_e.GET_ONLINE_USERS;
                    ((TCPClient) (MainActivity.mTcpClient)).msgToServer = "GET_ONLINE_USERS";
                    //Log.e("UpdateOnlineUsersList", "Trying to get online user list");
                    Object o = ((TCPClient) (MainActivity.mTcpClient)).GetOnlineUsers();

                    if (o instanceof OnlineUsers)
                        onlineUsersObject = (OnlineUsers) o;

                    if (onlineUsersObject == null) {
                        Log.e("UpdateOnlineUsersList", "Online Users is null");
                        //ServerFailed();
                    } else {
                        onlineUsersList = onlineUsersObject.GetOnlineUsers();
                        ArrayList<String> list = new ArrayList<String>();
                        User curr;
                        int currPermission;
                        hasOnlineUsers = false;
                        list.clear();

                        for (Map.Entry<String, User> entry : onlineUsersList.entrySet()) {//Update the list by nick names
                            curr = onlineUsersList.get(entry.getKey());

                            if(!userEmail.toLowerCase().equals(curr.email.toLowerCase())) {
                                list.add(curr.email.toLowerCase() + " ("+curr.nickName+")");
                                hasOnlineUsers = true;
                            }
                            else
                            {
                                currPermission = Integer.parseInt(curr.Permission);
                                MainActivity.userPermissionE = MainActivity.USER_PERMISSION_e.values()[currPermission];
                            }

                           /* //For test!!!
                            list.add(curr.email.toLowerCase() + " (" + curr.nickName + ")");
                            hasOnlineUsers = true;  */
                        }
                        if (!hasOnlineUsers)
                            list.add("No online users");

                        ArrayAdapter adapter = new ArrayAdapter(mContext,
                                android.R.layout.simple_list_item_1,list);

                        listview.setAdapter(adapter);




                    }
                }
                catch (Exception e)
                {
                    Log.e("UpdateOnlineUsers","Exception in UpdateOnlineUsers");
                }
    }

}