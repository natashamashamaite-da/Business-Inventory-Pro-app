package com.businesspro.inventorymanager;

//importing necessary android and java classes
import android.content.Intent; //it is used to navigate between activities
import android.os.Bundle; //it is used to pass data and manage activity state
import androidx.appcompat.app.AppCompatActivity; //this adds the base class for activities using the AppCompat library
import androidx.recyclerview.widget.LinearLayoutManager; //this is the layout manager to display items vertically in RecyclerView
import androidx.recyclerview.widget.RecyclerView; //this is the UI component to display lists efficiently
import com.google.android.material.bottomnavigation.BottomNavigationView; //this is the bottom navigation bar widget
import com.google.android.material.floatingactionbutton.FloatingActionButton; //this load the floating action button for quick actions
import java.util.List; // Used for handling collections like lists of sales

//this is the main Activity class for managing and displaying sales data
public class SalesActivity extends AppCompatActivity {

    //declaring variables for database operations and UI components
    private DatabaseHelper dbHelper; //handles database interactions (CRUD)
    private RecyclerView rvSales; //displays sales records in a list format

    //calling the onCreate method when the activity is created so it initialises UI and logic
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //it calls the parent class method to maintain lifecycle integrity
        setContentView(R.layout.activity_sales); //it also sts the layout xml file for this activity

        //it sets the title shown in the app bar
        getSupportActionBar().setTitle("Sales Management");

        //initialises database helper for accessing SQLite database
        dbHelper = new DatabaseHelper(this);

        //using findView method to find the RecyclerView from the xml layout
        rvSales = findViewById(R.id.rv_sales);

        //setting the layout manager to display items vertically (one below another)
        rvSales.setLayoutManager(new LinearLayoutManager(this));

        //using the findView method to find the floating action button used to add/record a new sale
        FloatingActionButton fab = findViewById(R.id.fab_record_sale);

        //when the FAB is clicked, it starts the RecordSaleActivity to record a new sale
        fab.setOnClickListener(v -> startActivity(new Intent(this, RecordSaleActivity.class)));

        //initialising the bottom navigation menu and set up navigation logic
        setupBottomNavigation();

        //loading and displaying the list of sales from the database
        loadSales();
    }

    //configuring the bottom navigation bar and handles tab switching between screens
    private void setupBottomNavigation() {
        //using findView to find the bottom navigation view in the layout
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        //setting the currently selected menu item (Sales tab)
        bottomNav.setSelectedItemId(R.id.nav_sales);

        //setting a listener to handle navigation when different tabs are selected
        bottomNav.setOnItemSelectedListener(item -> {
            // Gets the ID of the selected item
            int itemId = item.getItemId();

            //when the user navigates to the Dashboard screen
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class)); //start Dashboard activity
                overridePendingTransition(0, 0); //therno e's animation for transition
                finish(); //closing the current activity to avoid stacking
                return true;

                //when the user navigates to the Inventory screen
            } else if (itemId == R.id.nav_inventory) {
                startActivity(new Intent(this, InventoryActivity.class)); //start inventory activity
                overridePendingTransition(0, 0);
                finish();
                return true;

                //when the user navigates to the Reports screen
            } else if (itemId == R.id.nav_reports) {
                startActivity(new Intent(this, ReportsActivity.class)); //start reports activity
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            //return false if no valid menu item is selected
            return false;
        });
    }

    //Loading the sales records from the database and displays them in the RecyclerView
    private void loadSales() {
        //retrieving all sales from the database using the helper class
        List<Sale> sales = dbHelper.getAllSales();

        //creating an adapter to bind sales data to RecyclerView items
        SalesAdapter adapter = new SalesAdapter(this, sales);

        //setting the adapter to RecyclerView to display the data
        rvSales.setAdapter(adapter);
    }

    //calling the onResume method every time the activity becomes visible again (e.g., after recording a new sale)
    @Override
    protected void onResume() {
        super.onResume();

        //reloading the latest sales data to ensure the UI is up-to-date
        loadSales();
    }
}
