package com.example.user.chatclient;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.StrictMode;
import android.os.Bundle;
import android.os.Handler;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends BaseActivity {
    enum SERVER_ANSWER_e{NOT_RESPONDING,SERVER_FAILED,SERVER_SUCCESS};
    enum USER_PERMISSION_e{GUEST,USER,ADMIN};
    //-------------------------------Communication Variables-----------------------//
    private Handler handler;
    static Thread mTcpClient;
    static USER_PERMISSION_e userPermissionE;
    static SERVER_ANSWER_e serverStatus;
    static boolean waitToServerAnswer;
    static Context mContext;
    static HashMap<String,ArrayList<String>> historyChat;
    static HashMap<String, String> message;
    static String currentUser;
    static boolean hasConnected;
    private String getEmailText;
    //----------------------------------------------------------------------------//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AddButtonsAction();

        serverStatus = SERVER_ANSWER_e.NOT_RESPONDING;
        mContext = this;
        waitToServerAnswer = false;
        handler = new Handler();
        TextView txtStatus = (TextView)findViewById(R.id.txtStatus);
        txtStatus.setText("");

        System.out.println("Currect User "+currentUser);
        if(currentUser!=null && !currentUser.isEmpty())
        {
            startActivity(new Intent(MainActivity.this, NewUserActivity.class));//Switch to main user
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        hasConnected = false;

        historyChat = new HashMap<String,ArrayList<String>>();
        message = new HashMap<>();

    }

    //----------------------------------------------------------------------------//
    //-------------------------------Check Login Method---------------------------//
    //----------------------------------------------------------------------------//
    public void AddButtonsAction()
    {

        Button btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
               startActivity(intent);*/
                try {
                    TextView txtEmail = (TextView)findViewById(R.id.txtFEmail);
                    TextView txtPass = (TextView)findViewById(R.id.txtFPassword);
                    CheckBox remember = (CheckBox)findViewById(R.id.RememberMe);
                    getEmailText=txtEmail.getText().toString();
                    String Expn =
                            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                                    +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                    +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                                    +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                    +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                                    +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
                    if (getEmailText.matches(Expn) && getEmailText.length() > 0 && txtPass.getText().length()>0)
                    {
                        String encPass = EncryptUtils.base64encode( txtPass.getText().toString());
                        ConnectToServer("LOGIN "+getEmailText.toString() + " " +encPass);

                    }
                    else
                    {
                        TextView txtStatus = (TextView)findViewById(R.id.txtStatus);
                        txtStatus.setTextColor(Color.parseColor("#ffff003e"));
                        txtStatus.setText("You Need To Enter Email And Password!");
                    }
                }
                catch (Exception e)
                {
                    TextView txtStatus = (TextView)findViewById(R.id.txtStatus);
                    txtStatus.setTextColor(Color.parseColor("#ffff003e"));
                    txtStatus.setText("Server Is Down!\nTry Again Later...");
                    Log.e("AddButtonsAction","Exception");
                    ServerNotResponding();
                }
            }
        });
    }

    public void ConnectToServer(String msgFromClient) throws  InterruptedException
    {
        waitToServerAnswer = true;
        mTcpClient = new TCPClient();

        if(msgFromClient.equals("GUEST"))
        {
            getEmailText = "";
            userPermissionE = USER_PERMISSION_e.GUEST;
            ((TCPClient)(mTcpClient)).serverCommand = TCPClient.SERVER_COMMAND_e.GUEST;
        }
        else
        {
            userPermissionE = USER_PERMISSION_e.USER;
            ((TCPClient)(mTcpClient)).serverCommand = TCPClient.SERVER_COMMAND_e.CHECK_LOGIN;
        }
        ((TCPClient)(mTcpClient)).msgToServer = msgFromClient;
        Log.e("wait for run","...");
        mTcpClient.start();
        mTcpClient.join();
        Log.e("wait for run","after run");
        //When the thread back and we know the answer from the server
        ConnectionAnswer();
        waitToServerAnswer = false;
        Log.e("wait for run","after ConnectionAnswer");
    }
    //----------------------------------------------------------------------------//
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
    //----------------------------------------------------------------------------//
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
    //----------------------------------------------------------------------------//
    public void ServerFailed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        TextView txtStatus = (TextView)findViewById(R.id.txtStatus);

        hasConnected = false;

        System.out.println("Login Faild!");
        txtStatus.setTextColor(Color.parseColor("#ffff003e"));
        txtStatus.setText("Email/Password Incorrect, Please Try Again.");
        builder.setTitle("Login Faild");
        builder.setMessage("Email/Password Incorrect,\n Please Try Again.");
        builder.setPositiveButton("OK", null);
        dialog = builder.create();
        dialog.show();
    }
    //----------------------------------------------------------------------------//
    public void ServerSuccess()
    {
        TextView txtStatus = (TextView)findViewById(R.id.txtStatus);
        System.out.println("Login Success!");
        txtStatus.setText("");
        TextView txt = (TextView)findViewById(R.id.txtFEmail);
        currentUser = txt.getText().toString();
        //((TCPClient)(mTcpClient)).StopComm();

        hasConnected = true;

        Intent login = new Intent(mContext, MainChatActivity.class);//Switch to main activity
        login.putExtra("sourceEmail",getEmailText.toString());
        mContext.startActivity(login);
    }
    //----------------------------------------------------------------------------//

    //----------------------------------------------------------------------------//
    //---------------------------End Of Connect Methods---------------------------//
    //----------------------------------------------------------------------------//

    //----------------------------------------------------------------------------//
    //----------------------------Guest Methods-----------------------------------//
    //----------------------------------------------------------------------------//

    public void OnGuestClick(View v)
    {
       try
       {
           ConnectToServer("GUEST");
           if(serverStatus != SERVER_ANSWER_e.SERVER_SUCCESS)
               return;
       }
        catch(InterruptedException e)
        {
            Log.e("OnGuestClick","Exception!!");
            e.printStackTrace();
            return;
        }

        Intent login = new Intent(mContext, MainChatActivity.class);//Switch to main activity
        login.putExtra("destEmail", "");
        login.putExtra("sourceEmail", "");
        mContext.startActivity(login);
    }
    //----------------------------------------------------------------------------//
    //---------------------------End Of Guest Methods-----------------------------//
    //----------------------------------------------------------------------------//

    //----------------------------------------------------------------------------//
    //---------------------------New User Methods---------------------------------//
    //----------------------------------------------------------------------------//
    public void OnNewUserClick(View v)
    {
        if(!waitToServerAnswer)
            startActivity(new Intent(MainActivity.this, NewUserActivity.class));
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please Wait");
            builder.setMessage("Please Wait..You Are Just Trying To Login");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.show();
        }
    }
    //----------------------------------------------------------------------------//
    //---------------------------Enf Of New User Methods-------------------------//
    //----------------------------------------------------------------------------//

    //----------------------------------------------------------------------------//
    //---------------------------Forget Password Methods--------------------------//
    //----------------------------------------------------------------------------//
    public void OnForgetPasswordClick(View v)
    {
        if(!waitToServerAnswer)
            startActivity(new Intent(MainActivity.this, ForgetPasswordActivity.class));
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please Wait");
            builder.setMessage("Please Wait..You Are Just Trying To Login");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.show();
        }
    }
    //----------------------------------------------------------------------------//
    //-----------------------Enf Of Forgers Password Methods----------------------//
    //----------------------------------------------------------------------------//
    public void OnContactUsClick(View v)
    {
        /*Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto: evyatar98@gmail.com"));
        intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contact From Application");
        intent.putExtra(Intent.EXTRA_TEXT, "Your Text Here.");

        startActivity(Intent.createChooser(intent, "Send Email"));*/

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","evyatar98@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact From Chat Application");
        startActivity(Intent.createChooser(emailIntent, "Chat Application, Contact Us"));

    }

    //----------------------------------------------------------------------------//
    //---------------------------Contact Us Methods-------------------------------//
    //----------------------------------------------------------------------------//

    //----------------------------------------------------------------------------//
    //---------------------------Contact Us Methods-------------------------------//
    //----------------------------------------------------------------------------//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

}
