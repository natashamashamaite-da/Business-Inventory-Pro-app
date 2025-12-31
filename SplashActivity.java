package com.businesspro.inventorymanager;

//importing statements to include the necessary Android classes
import android.content.Intent; //this is used to switch between activities (screens)
import android.os.Bundle; //this is used to pass data between activities and manage saved states
import android.os.Handler; //this is used to execute code after a specified delay
import androidx.appcompat.app.AppCompatActivity; //creating a base class for activities that use the AppCompat support library

//splashActivity class that extends AppCompatActivity
//this activity displays a splash screen when the app starts before moving to the main dashboard
public class SplashActivity extends AppCompatActivity {

    //calling the onCreate() method is called when this activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //this calls the parent class's onCreate() to set up the activity lifecycle

        //sets the UI layout for this activity to the file "activity_splash.xml"
        //this xml defines the splash screen's design (logo, background color)
        setContentView(R.layout.activity_splash);

        //creating a new Handler that will execute code after a short delay
        //this delay allows the splash screen to be visible for a few seconds before continuing
        new Handler().postDelayed(new Runnable() {

            //the runnable defines the code that should run after the delay
            @Override
            public void run() {
                //creating an Intent to navigate from SplashActivity → DashboardActivity
                //this means once the splash screen finishes, the user is taken to the main dashboard
                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));

                //finish the SplashActivity so that the user cannot go back to it using the back button
                finish();
            }

            //the delay is set to 2500 milliseconds (2.5 seconds)
        }, 2500);
    }
}
