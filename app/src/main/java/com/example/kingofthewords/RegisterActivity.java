package com.example.kingofthewords;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kingofthewords.dbModels.User;

import org.litepal.LitePal;

public class RegisterActivity extends AppCompatActivity {

    //all the elements in this activity
    private EditText mEtUsername;
    private EditText mEtEmail;
    private EditText mEtPassword1;
    private EditText mEtPassword2;
    private Button mBtnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //find the elements in layout file (find views)
        mEtUsername = findViewById(R.id.et_username);
        mEtEmail = findViewById(R.id.et_email);
        mEtPassword1 = findViewById(R.id.et_password1);
        mEtPassword2 = findViewById(R.id.et_password2);
        mBtnConfirm = findViewById(R.id.btn_confirm);

        //set the click listener for the confirm button
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we will check the user input to register the user
                registerCheck();
            }
        });
    }

    /**
     * define what should be done when the back button 'HomeAsUp' is clicked.
     * we let it go back to the last activity
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //call the finish() method to close the current activity and go back to the last activity (login)
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method will check the user input and register the user.
     * Everything should not be null or empty string
     * User email should be unique
     * Two passwords should match
     */
    private void registerCheck(){
        //get the user input
        String username = mEtUsername.getText().toString();
        String email = mEtEmail.getText().toString();
        String password1 = mEtPassword1.getText().toString();
        String password2 = mEtPassword2.getText().toString();

        //check if there are anything empty
        if(isEmptyBlank(username) || isEmptyBlank(email) || isEmptyBlank(password1) || isEmptyBlank(password2)){
            //if any of them is empty, we will reject the registration
            Toast.makeText(RegisterActivity.this, "You should fill all the blanks", Toast.LENGTH_SHORT).show();
        }else{
            //go on to check whether the email has already been used
            User userWithSameEmail = LitePal.where("email = ?", email).findFirst(User.class);
            if (userWithSameEmail != null){
                //if a user is fund, we will reject the registration
                Toast.makeText(RegisterActivity.this, "The email has already been used", Toast.LENGTH_SHORT).show();
            }else {
                //go on to check the two pass words
                if (!password1.equals(password2)){
                    //if two passwords not match
                    Toast.makeText(RegisterActivity.this, "Two passwords not match", Toast.LENGTH_SHORT).show();

                }else{
                    //the user input passed all the checking, now we can register it into the database
                    User userObj = new User();
                    userObj.setUsername(username);
                    userObj.setEmail(email);
                    userObj.setPassword(password1);
                    userObj.save();
                    Toast.makeText(RegisterActivity.this, "Register successfully, now you can login", Toast.LENGTH_SHORT).show();

                    //transfer the user to the log in activity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    /**
     * This method is used to check whether a string is empty
     * "" or " " or null
     * @param s the string to be checked
     * @return true for empty, false for not empty
     */
    private boolean isEmptyBlank(String s){
        if (s == null){
            return true;

        }else{
            s = s.trim();
            if (s.equals("")){
                return true;
            }else{
                return false;
            }
        }
    }



}