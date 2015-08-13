package com.example.user.chatclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class ChatRoomActivity extends BaseActivity {
    enum SERVER_ANSWER_e{NOT_RESPONDING,SERVER_FAILED,SERVER_SUCCESS};
   //___________________________________________________________________________
    private EditText messageET;
    private Runnable receiverThread;
    private ListView messagesContainer;
    private Button sendBtn;
    String sourceEmail;
    private boolean waitingForMessage;
    static boolean waitToServerAnswer;
    static boolean keepGetMessages;
    static SERVER_ANSWER_e serverStatus;
    static Context mContext;
    static String destEmail;
    static ArrayList<String> msgList;
    boolean EditButtonImage=false;
    static boolean oneThread=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        TextView DestTextView = (TextView) findViewById(R.id.textViewFriend);

        mContext = this;
        keepGetMessages = true;
        waitingForMessage = true;
        Bundle b = getIntent().getExtras();
        destEmail= b.getString("destEmail");
        sourceEmail=b.getString("sourceEmail");
        initControls(destEmail,sourceEmail);
        msgList = new ArrayList<String>();



        receiverThread = new ReceiveMessages(((TCPClient) (MainActivity.mTcpClient)));
        final Handler mHandler = new Handler();
        final Button EditBtn = (Button)findViewById(R.id.editbutton);
        ImageView TAppIMG = (ImageView)findViewById(R.id.icon_image);
        final Button LogOutBtn = (Button)findViewById(R.id.logoutBtn);
        final TextView TAppTitle = (TextView)findViewById(R.id.txtMainChat);
        TAppTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });
        LogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TCPClient) (MainActivity.mTcpClient)).serverCommand = TCPClient.SERVER_COMMAND_e.STOP_COMM;
                ((TCPClient) (MainActivity.mTcpClient)).msgToServer = "DISCONNECT " + sourceEmail ;//append email
                ((TCPClient) (MainActivity.mTcpClient)).DisconnectFromServer();
                MainChatActivity.toFinish = true;
                Log.e("Disconnect", "Disconnect from server");
                finish();


            }
        });
        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(ChatRoomActivity.this, settingActivity.class);
                edit.putExtra("User",sourceEmail);
                startActivity(edit);
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

        final Runnable mUpdateResults = new Runnable() {
            public void run() {
                    GetNewMessage();
        }};

        int delay = 700; // delay for 1 sec.
        int period = 1000; // repeat every 4 sec.
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                mHandler.post(mUpdateResults);;
            }

        }, delay, period);
        if(!oneThread)
        {
            Thread t = new Thread(receiverThread);
            t.start();
        }
        DestTextView.setText(destEmail);
     }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void  initControls(final String destEmail, final String sourceEmail)
    {
        sendBtn = (Button) findViewById(R.id.btuSend);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    messagesContainer = (ListView) findViewById(R.id.listViewChat);
                    messageET = (EditText) findViewById(R.id.txtSendMes);
                    String encMsg = EncryptUtils.base64encode(messageET.getText().toString());
                    String messageText = "SEND_MESSAGE`" +sourceEmail +"`"+ destEmail +"`"+encMsg;
                    if (messageET.length() > 0)
                    {
                        ((TCPClient) (MainActivity.mTcpClient)).SendMessage(messageText);
                        AddMessageToList(messageET.getText().toString());
                    }

                    else
                    {
                        EmptyMessageHandle();
                    }
                } catch (Exception e) {

                    //ServerNotResponding();
                }
            }
        });
    }

    public void AddMessageToList(String messageText)
    {
        msgList.add("Me: "+messageText);
        messageET.setText("");
        TextView msg = (TextView)findViewById(R.id.MyMessageTV);
        msg.setText("Me: "+messageText);
        ListView listview = (ListView) findViewById(R.id.listViewChat);



        /*ArrayAdapter adapter = new ArrayAdapter(mContext,
                android.R.layout.simple_list_item_1, msgList);

        listview.setAdapter(adapter);*/

        if(MainActivity.historyChat.containsKey(destEmail.toLowerCase()))
        {


                MainActivity.historyChat.get(destEmail.toLowerCase()).add("Me: " + messageText);
                ArrayAdapter adapter = new ArrayAdapter(mContext,
                        R.layout.chat, MainActivity.historyChat.get(destEmail.toLowerCase()));
                listview.setAdapter(adapter);
                for(int i=0;i<MainActivity.historyChat.size();i++)
                {
                    if(MainActivity.historyChat.get(i).contains("Me: "))
                    {

                    }
                }



        }
        else
        {
            MainActivity.historyChat.put(destEmail.toLowerCase(), new ArrayList<String>());
            MainActivity.historyChat.get(destEmail.toLowerCase()).add("Me: "+messageText );
            ArrayAdapter adapter = new ArrayAdapter(mContext,
                    R.layout.chat, MainActivity.historyChat.get(destEmail.toLowerCase()));

            listview.setAdapter(adapter);
        }
        AutoScroll(listview);

    }
    public void GetNewMessage()
    {
        /*ListView listview = (ListView) findViewById(R.id.listViewChat);
        ArrayAdapter adapter = new ArrayAdapter(mContext,
                android.R.layout.simple_list_item_1, msgList);
        listview.setAdapter(adapter);*/
        if(MainActivity.historyChat.containsKey(destEmail.toLowerCase()))
        {
            ListView listview = (ListView) findViewById(R.id.listViewChat);
            ArrayAdapter adapter = new ArrayAdapter(mContext,
                    R.layout.mymessage, MainActivity.historyChat.get(destEmail.toLowerCase()));
            listview.setAdapter(adapter);
            AutoScroll(listview);
        }


    }
    public void ConnectionAnswer()
    {
        switch(serverStatus)
        {
            case NOT_RESPONDING:
                ServerNotResponding();
                break;
            case SERVER_FAILED:
                ServerFailed();
                break;
            case SERVER_SUCCESS:
                ServerSuccess();
                break;
            default:
                break;
        }

    }
    private void EmptyMessageHandle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;

        System.out.println("Empty message");
        builder.setTitle("Empty message");
        builder.setMessage("Please enter a message and then click Send");
        builder.setPositiveButton("OK", null);
        dialog = builder.create();
        dialog.show();
    }

    public void  ServerNotResponding(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;

        System.out.println("Server Isn't Responding!");
        builder.setTitle("Server Isn't Responding");
        builder.setMessage("Server Isn't Responding, Please Try Again Later...");
        builder.setPositiveButton("OK", null);
        dialog = builder.create();
        dialog.show();
    }
    public void ServerFailed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        TextView txtStatus = (TextView)findViewById(R.id.txtStatus);

        System.out.println("Login Faild!");
        txtStatus.setTextColor(Color.parseColor("#ffff003e"));
        txtStatus.setText("Email/Password Incorrect, Please Try Again.");
        builder.setTitle("Login Faild");
        builder.setMessage("Email/Password Incorrect,\n Please Try Again.");
        builder.setPositiveButton("OK", null);
        dialog = builder.create();
        dialog.show();
    }
    //---------------------------------//
    public void ServerSuccess()
    {
        ///**********************
    }

    private void AutoScroll(final ListView listView)
    {
        listView.post( new Runnable() {
            @Override
            public void run() {
                listView.setSelection(listView.getCount()-1);
            }
        });
    }

}