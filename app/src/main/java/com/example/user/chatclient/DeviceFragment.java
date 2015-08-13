package com.example.user.chatclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by adir schlezinger on 27/05/2015.
 */
public class DeviceFragment extends android.support.v4.app.Fragment {
    public static final String BackgroundIDKey="imagekey";
    public static final String DescriptionKey="descriptionkey";
    public static final String NavKey="navkey";
    public static final String BtnVisability="btnvisabilty";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trans_app,container,false);
        Button btnSignin = (Button)view.findViewById(R.id.btnSignIn);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),MainActivity.class);
                startActivity(intent);

            }
        });
        Button btnJoin = (Button)view.findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),NewUserActivity.class);
                startActivity(intent);

            }
        });
        Bundle bundel = getArguments();
        if(bundel != null)
        {
            int BackgroundID = bundel.getInt(BackgroundIDKey);
            int navID = bundel.getInt(NavKey);
            int btnV = bundel.getInt(BtnVisability);
            String decription = bundel.getString(DescriptionKey);

            setValues(view, BackgroundID , navID , btnV , decription);
        }

        return view;
    }

    private void setValues(View view, int BackgroundID, int navID , int btnv , String decription) {
        RelativeLayout layout =(RelativeLayout)view.findViewById(R.id.TransAppRelative);
        layout.setBackgroundResource(BackgroundID);
        Button btnSign = (Button)view.findViewById(R.id.btnSignIn);
        btnSign.setVisibility(btnv);
        Button btnJoin = (Button)view.findViewById(R.id.btnJoin);
        btnJoin.setVisibility(btnv);
        TextView textView = (TextView) view.findViewById(R.id.textView6);
        textView.setText(decription);
        ImageView imageView = (ImageView)view.findViewById(R.id.startimageView);
        imageView.setImageResource(navID);

    }
}
