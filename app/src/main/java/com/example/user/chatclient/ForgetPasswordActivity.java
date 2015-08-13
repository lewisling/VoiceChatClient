    package com.example.user.chatclient;

    import android.app.AlertDialog;
    import android.support.v7.app.ActionBarActivity;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.Button;
    import android.widget.TextView;


    public class ForgetPasswordActivity extends ActionBarActivity {

        private Thread mTcpClient;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_forget_password);
            AddButtonsAction();

        }


        public void AddButtonsAction()
        {
            Button btnConnect = (Button)findViewById(R.id.btnSendPassword);
            btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        TextView txtEmail = (TextView)findViewById(R.id.txtFEmailForgetPassword);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this);
                        AlertDialog dialog;
                    if(txtEmail.getText().toString().isEmpty())
                    {
                        builder.setTitle("Email Field Is Empty!");
                        builder.setMessage("Email Field Is Empty!");
                        builder.setPositiveButton("OK", null);
                        dialog = builder.create();
                        dialog.show();
                        return;
                    }
                    mTcpClient = new TCPClient();

                     ((TCPClient)(mTcpClient)).serverCommand = TCPClient.SERVER_COMMAND_e.FORGET_PASSWORD;


                     ((TCPClient)(mTcpClient)).msgToServer = "FORGET "+txtEmail.getText().toString();

                    mTcpClient.start();
                    mTcpClient.join();


                        switch (MainActivity.serverStatus) {
                            case NOT_RESPONDING:
                                Log.e("Forget Password", "Server Isn't Responding!\n");
                                System.out.println("Server Isn't Responding!");
                                builder.setTitle("Server Isn't Responding");
                                builder.setMessage("Server Isn't Responding, Please Try Again Later...");
                                builder.setPositiveButton("OK", null);
                                dialog = builder.create();
                                dialog.show();

                                break;
                            case SERVER_FAILED:
                                Log.e("Forget Password", "Forget Password Failed To Mail "+txtEmail.getText().toString()+"\n");
                                System.out.println("Forget Password Failed To Mail "+txtEmail.getText().toString());
                                builder.setTitle("Forget Password");
                                builder.setMessage("The Email Does Not Exist In The System");
                                builder.setPositiveButton("OK", null);
                                dialog = builder.create();
                                dialog.show();
                                break;
                            case SERVER_SUCCESS:
                                Log.e("Forget Password", "Forget Password Success To Mail "+txtEmail.getText().toString()+"\n");
                                System.out.println("Forget Password Success To Mail "+txtEmail.getText().toString());
                                builder.setTitle("Forget Password");
                                builder.setMessage("The New Password Was Sent To You By Email");
                                builder.setPositiveButton("OK", null);
                                dialog = builder.create();
                                dialog.show();
                                break;
                            default:
                                break;
                    }


                }

                catch(Exception e)
                {
                    Log.e("New User Reg", "Error In Forget Password\n");
                    System.out.println(e.fillInStackTrace());
                }}

            });
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_forget_password, menu);
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
