package com.example.chaya.medprotest;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;


public class PharmacyLoginActivityTest extends android.test.ActivityUnitTestCase<PharmacyLoginActivity> {

    EditText usernameText;
    private PharmacyLoginActivity activity;
    private TextView passwordtext;
    private Button loginButton;

    public PharmacyLoginActivityTest() {
        super(PharmacyLoginActivity.class);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation().getTargetContext(), PharmacyLoginActivity.class);
        startActivity(intent, null, null);
        activity = getActivity();
    }

    public void testLayout() {

        //Extracting View Elements from the layout
        usernameText = (EditText) activity.findViewById(R.id.login_username);
        passwordtext = (TextView) activity.findViewById(R.id.login_password);
        loginButton = ( Button) activity.findViewById(R.id.login_button);

        //Asserting whether elements are not null
        assertNotNull("This cannot be null", usernameText);
        assertNotNull("This cannot be null", passwordtext);
        assertNotNull("This cannot be null", loginButton);
    }


    public void testLogin(){
        //Extracting View Elements from the layout
        usernameText = (EditText) activity.findViewById(R.id.login_username);
        passwordtext = (TextView) activity.findViewById(R.id.login_password);
        loginButton = ( Button) activity.findViewById(R.id.login_button);

        //Asserting the views are not null
        assertNotNull("This cannot be null", usernameText);
        assertNotNull("This cannot be null", passwordtext);
        assertNotNull("This cannot be null", loginButton);

        //Setting the username and password
        usernameText.setText("idiz");
        passwordtext.setText("123");

        //Click the login button
        loginButton.performClick();

        //Retrieving the started activity, after pressing thr login button.
        Intent startedActivityIntent = getStartedActivityIntent();

        //Ensuring the started activity is not null. i.e login is successful.
        assertNotNull("Doctor Login Failed", startedActivityIntent);
    }
} 