package com.example.kingofthewords;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kingofthewords.dbModels.User;
import com.example.kingofthewords.util.ReadJsonIntoDB;

import org.litepal.LitePal;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //all the elements in this activity
    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private Button mBtnGoRegister;

    //we use 'shared preference' to store info data of the user who has logged in, using 'editor' to write data in to this file
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //initialize the shared preference file
        this.pref = getSharedPreferences("data", MODE_PRIVATE);
        //initialize the editor
        editor = pref.edit();

        //initialize all the elements (find views)
        mEtEmail = findViewById(R.id.et_email);
        mEtPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnGoRegister = findViewById(R.id.btn_go_register);

        //set click listeners for the buttons
        mBtnLogin.setOnClickListener(this);
        mBtnGoRegister.setOnClickListener(this);

    }

    //set click listeners for the buttons
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //if the login button is pressed
            case R.id.btn_login:
                //check the user input and log him in
                loginCheck();
                break;

            //if the btn_go_register button is pressed,
            case R.id.btn_go_register:
                // we transfer the user to the registration activity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * This method will be called when user clicking on the login button
     * This can check the user input data with our database
     * If the uer email and password are matched, we let them log in
     * If not, we just refuse the login request
     */
    private void loginCheck(){
        //get the user input
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();

        //query the uer object from our data base by matching the email
        User user = LitePal.where("email = ?", email).findFirst(User.class);
        //if the user object is fund in our database by matching the email
        if (user != null){
            //go on to check the password
            if (user.getPassword().equals(password)){
                //if the password matched, the user logs in successfully
                //store the user info data into the shared preference file
                editor.putInt(MainActivity.USER_ID, user.getId());
                editor.apply();                //submit the editing of the file

                //transfer the user to our main activity
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            }else {
                //if the password not matched
                Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
            }

        //if there are no user with this email in our database
        }else{
            Toast.makeText(LoginActivity.this, "User info not fund, check you email again", Toast.LENGTH_SHORT).show();
        }

    }
}