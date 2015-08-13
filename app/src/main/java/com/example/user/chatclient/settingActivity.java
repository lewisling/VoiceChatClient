package com.example.user.chatclient;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class settingActivity extends ActionBarActivity {

    private User user=null;
    static enum SERVER_ANSWER_e{NOT_RESPONDING,SERVER_FAILED,SERVER_SUCCESS};
    static SERVER_ANSWER_e serverStatus;
    static String perrmission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Bundle b = getIntent().getExtras();
        String userEmail=b.getString("User");
        perrmission= b.getString("perr");
        this.user= MainChatActivity.onlineUsersList.get(userEmail);
        //User=MainActivity.
        Spinner sLang = (Spinner) findViewById(R.id.spinnerLang);
        String [] arraySpinner = new String[] {
                "HEBREW", "ENGLISH", "RUSSIAN", "ARABIC"
        };
        ArrayAdapter<String> adapterLang = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        sLang.setAdapter(adapterLang);

        Spinner spinnerPerr= (Spinner)findViewById(R.id.PerrmissionSpinner);
        String[] perrArray=new String []{"Admin","Basic User"};
        ArrayAdapter<String> adapterperr = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, perrArray);
        spinnerPerr.setAdapter(adapterperr);



        UpdateExistingDetails();
        AddButtonsAction();
    }

    public void AddButtonsAction()
    {
        Button updateBtn=(Button)findViewById(R.id.btnUpdate);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String MesToServer = "UPDATE ";
                TextView email= (TextView)findViewById(R.id.txtViewEmail);
                MesToServer+=email.getText()+",";
                EditText firstName = (EditText) findViewById(R.id.editTextFirstName);
                MesToServer += firstName.getText().toString() + ",";
                EditText lastName = (EditText) findViewById(R.id.editTextLastName);
                MesToServer += lastName.getText().toString() + ",";
                EditText nickName = (EditText) findViewById(R.id.editTextNickName);
                MesToServer += nickName.getText().toString() + ",";
                EditText age = (EditText) findViewById(R.id.editTextAge);
                MesToServer += age.getText().toString() + ",";
                Spinner spinner = (Spinner) findViewById(R.id.spinnerLang);
                MesToServer += spinner.getSelectedItem().toString()+",";
                if(perrmission.equals("1"))
                {
                    MesToServer+="1";
                }
                else
                {
                    Spinner spinner2 = (Spinner) findViewById(R.id.PerrmissionSpinner);
                    if(spinner2.getSelectedItem().toString()== "Admin")
                        MesToServer+="2";
                    else
                        MesToServer+="1";
                }
                try {
                    if (  firstName.length() > 0 && lastName.length() > 0 && nickName.length() > 0 && age.length() > 0 ) {

                        ((TCPClient)MainActivity.mTcpClient).updateUser(MesToServer);

                        switch (serverStatus) {
                            case NOT_RESPONDING:
                                ServerNotResponding();
                                break;
                            case SERVER_FAILED:
                                serverFaild();
                                break;
                            case SERVER_SUCCESS:
                                serverSuccess();
                                break;
                            default:
                                break;
                        }
                    } else {
                        TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
                        txtStatus.setTextColor(Color.parseColor("#ffff003e"));
                        txtStatus.setText("You Need To Enter vallue !");
                    }
                } catch (Exception e) {

                    Log.e("User Update", "Error In Update \n");
                    System.out.println(e.fillInStackTrace());
                }
            }
        });
    }
    public void serverSuccess()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(settingActivity.this);
        AlertDialog dialog;
        System.out.println("Update Success!");
        builder.setTitle("Update Success!");
        builder.setMessage("Update Success!");
        builder.setPositiveButton("OK", null);
        dialog = builder.create();
        dialog.show();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finish();

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
    public  void serverFaild()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(settingActivity.this);
        AlertDialog dialog;
        System.out.println("Update Faild!");
        builder.setTitle("Update Faild!");
        builder.setMessage("Email/Nick Name Already In Using,Please Try Another Email/Nick Name.");
        builder.setPositiveButton("OK", null);
        dialog = builder.create();
        dialog.show();
    }
    public void UpdateExistingDetails()
    {

        TextView email=(TextView) findViewById(R.id.txtViewEmail);
        EditText firstName= (EditText)findViewById(R.id.editTextFirstName);
        EditText lastName= (EditText)findViewById(R.id.editTextLastName);
        EditText nickName= (EditText)findViewById(R.id.editTextNickName);
        EditText age= (EditText)findViewById(R.id.editTextAge);
        Spinner spinner = (Spinner)findViewById(R.id.spinnerLang);
        email.setText(this.user.email);
        firstName.setText(this.user.firstName);
        lastName.setText(this.user.lastName);
        nickName.setText(this.user.nickName);
        age.setText(this.user.Age);
        ArrayAdapter<String> spinnerAdapter=( ArrayAdapter<String>) spinner.getAdapter();
        spinner.setSelection(spinnerAdapter.getPosition(this.user.lang));
        Spinner spinner2 = (Spinner)findViewById(R.id.PerrmissionSpinner);
        TextView Perr= (TextView) findViewById(R.id.txtPerr);
        if( perrmission.equals("2"))
        {

            ArrayAdapter<String> spinnerAdapter2 =(ArrayAdapter<String>)spinner2.getAdapter();
            if(user.Permission.equals("1"))
            {

                spinner.setSelection(spinnerAdapter2.getPosition("Basic User"));
            }
            else
            {
                spinner.setSelection(spinnerAdapter2.getPosition("Admin"));
            }
            Perr.setVisibility(View.VISIBLE);
            spinner2.setVisibility(View.VISIBLE);
        }
        else
        {
            spinner2.setVisibility(View.GONE);
            Perr.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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