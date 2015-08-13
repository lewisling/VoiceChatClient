package com.example.user.chatclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


public class NewUserActivity extends ActionBarActivity {

    private Thread mTcpClient;
    static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        AddButtonsAction();

        String [] arraySpinner = new String[] {
                "HEBREW", "ENGLISH", "RUSSIAN", "ARABIC"
        };
        Spinner s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);
        mContext = this;
    }
    //--------------------------------------------------------------------------//
    public void AddButtonsAction()
    {
        Button btnConnect = (Button)findViewById(R.id.btnRegister);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mTcpClient = new TCPClient();

                    ((TCPClient)(mTcpClient)).serverCommand = TCPClient.SERVER_COMMAND_e.INSERT;
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewUserActivity.this);
                    AlertDialog dialog;

                    if(BuildRegistrationString()!="") {

                        ((TCPClient)(mTcpClient)).msgToServer = BuildRegistrationString();
                        mTcpClient.start();
                        mTcpClient.join();

                        switch(MainActivity.serverStatus)
                        {
                            case NOT_RESPONDING:
                                System.out.println("Server Isn't Responding!");
                                builder.setTitle("Server Isn't Responding");
                                builder.setMessage("Server Isn't Responding, Please Try Again Later...");
                                builder.setPositiveButton("OK", null);
                                dialog = builder.create();
                                dialog.show();

                                break;
                            case SERVER_FAILED:
                                System.out.println("Registration Faild!");
                                builder.setTitle("Registration Faild!");
                                builder.setMessage("Email/Nick Name Already In Using,Please Try Another Email/Nick Name.");
                                builder.setPositiveButton("OK", null);
                                dialog = builder.create();
                                dialog.show();
                                break;
                            case SERVER_SUCCESS:
                                System.out.println("Regisration Success!");
                                System.out.println("Registration Success!");
                                builder.setTitle("Registration Success!");
                                builder.setMessage("Registration Success,Welcome!");
                                builder.setPositiveButton("OK", null);
                                dialog = builder.create();
                                dialog.show();
                                Intent login = new Intent(mContext, MainActivity.class);
                                mContext.startActivity(login);
                                break;
                            default:
                                break;
                        }
                    }
                    else
                    {
                        Log.e("New User Reg", "One Or More Fields Missed\n");
                        builder = new AlertDialog.Builder(NewUserActivity.this);
                        builder.setTitle("New User Registration");
                        builder.setMessage("One Or More Fields Missed!");
                        builder.setPositiveButton("OK", null);
                        dialog = builder.create();
                        dialog.show();

                    }


                } catch (Exception e) {
                    Log.e("New User Reg", "Error In Registration\n");
                    System.out.println(e.fillInStackTrace());
                }
            }
        });
    }
    //--------------------------------------------------------------------------//


    public String BuildRegistrationString()
    {
        TextView temp;
        String regStr="INSERT '";

        temp = (TextView)findViewById(R.id.txtFEmail);
        String getText=temp.getText().toString().toLowerCase();
        String Expn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        if (getText.matches(Expn) && getText.length() > 0 )
        {
            regStr += getText.toString() + "','";
        }
        else {
            temp.requestFocus();
            temp.setError("ENTER ONLY VALID EMAIL");
            return "";
        }

        temp = (TextView)findViewById(R.id.txtFNickName);
        if(!temp.getText().toString().isEmpty()) {
            regStr += temp.getText().toString() + "','";
        }
        else
            return "";

        temp = (TextView)findViewById(R.id.txtFPassword);
        if(!temp.getText().toString().isEmpty()) {
            regStr += temp.getText().toString() + "','";
        }
        else
            return "";

        temp = (TextView)findViewById(R.id.txtFFirstName);
        if(!temp.getText().toString().isEmpty()) {
            regStr += temp.getText().toString() + "','";
        }
        else
            return "";

        temp = (TextView)findViewById(R.id.txtFLastName);
        if(!temp.getText().toString().isEmpty()) {
            regStr += temp.getText().toString() + "','";
        }
        else
            return "";

        temp = (TextView)findViewById(R.id.txtFPhone);
        getText=temp.getText().toString();
        if(getText.matches("[0-9]+") && getText.length() > 0)
        {
            regStr += temp.getText().toString()+"','";
        }
        else {
            temp.requestFocus();
            temp.setError("ENTER ONLY NUMBERS");
            return "";
        }


        temp = (TextView)findViewById(R.id.txtFAge);
        if(!temp.getText().toString().isEmpty()) {
            regStr += temp.getText().toString() + "','";
        }
        else
            return "";

        regStr +="1','";//Permission - Default is USER (GUEST=0,USER=1,ADMIN=2)

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        regStr += spinner.getSelectedItem().toString()+"'";
        Log.e("New User",regStr);

        System.out.println(regStr);
        return regStr;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_user, menu);
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
