package com.example.user.chatclient;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Map;


public class updateUsersAdmin extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_users_admin);
        updateUserList();
        addButtonLissner();
    }

    public void addButtonLissner()
    {
        Button updateBtn=(Button)findViewById(R.id.btnUpdateUser);
                try {
                updateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Spinner spinner= (Spinner)findViewById(R.id.spinnerUsers);
                        Intent edit = new Intent(updateUsersAdmin.this, settingActivity.class);
                        edit.putExtra("User",spinner.getSelectedItem().toString());
                        edit.putExtra("perr","2");
                        startActivity(edit);
                            }
                });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

    }

    public void updateUserList()
    {
        try {
            boolean flag = true;
            ArrayList<String> list = new ArrayList<String>();
            for (Map.Entry<String, User> entry : MainChatActivity.onlineUsersList.entrySet()) {
                list.add(entry.getKey());
                flag = false;
            }
            if (flag)
                list.add("No online users");
            Spinner spinner= (Spinner)findViewById(R.id.spinnerUsers);
            SpinnerAdapter adapter = new ArrayAdapter(updateUsersAdmin.this, android.R.layout.simple_spinner_item, list);
            spinner.setAdapter(adapter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_users_admin, menu);
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
