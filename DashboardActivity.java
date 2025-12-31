package com.businesspro.inventorymanager;

// importing all the required android classes and libraries for functionality and UI (Medium.com, 2025)
import android.content.Intent; // this will be used for navigating between activities (screens)
import android.os.Bundle; // this will be used to store activity state between re-creations
import android.widget.TextView; // this is for displaying text data on the screen
// importing AndroidX support libraries for modern app compatibility
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// importing Material Design components for the modern bottom navigation bar
import com.google.android.material.bottomnavigation.BottomNavigationView;
// importing the utility classes for formatting and list management (Medium.com, 2025)
import java.text.NumberFormat; // this is used to display numbers as properly formatted currency
import java.util.List; // this is used for storing and working with lists of objects
import java.util.Locale; // this is used to format currency for a specific region

// creating a public class for the eDashboardActivity class to display business summary info and recent sales
public class DashboardActivity extends AppCompatActivity {

    // declaring the database helper that will connect to the sqlite database
    private DatabaseHelper dbHelper;

    // declaring the TextView elements to show dashboard statistics
    private TextView tvTotalProducts, tvTotalSales, tvInventoryValue, tvLowStock;

    // declaring the RecyclerView to display a scrollable list of recent sales
    private RecyclerView rvRecentSales;

    // Declaring the formatter to display prices and money values in South African Rand format
    private NumberFormat currencyFormat;

    // onCreate() will be called when this activity starts
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting the layout file (activity_dashboard.xml) that defines the UI
        setContentView(R.layout.activity_dashboard);

        // setting the title of the action bar at the top of the screen
        getSupportActionBar().setTitle("Dashboard");

        // initialising the database helper so I can access stored product/sales data (Code, 2024)
        dbHelper = new DatabaseHelper(this);

        // setting the  currency format for South Africa (en_ZA)
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));

        // callin the helper methods to initialize components, set up navigation, and load data
        initViews();
        setupBottomNavigation();
        loadDashboardData();
    }

    // creating a method to connect UI elements from the layout file to variables in this class (Codepath.com, 2025)
    private void initViews() {
        // Linking each TextView to its ID in the layout
        tvTotalProducts = findViewById(R.id.tv_total_products);   //this displays the total number of products
        tvTotalSales = findViewById(R.id.tv_total_sales);         // this displays the total sales amount
        tvInventoryValue = findViewById(R.id.tv_inventory_value); // this displays the total inventory value
        tvLowStock = findViewById(R.id.tv_low_stock);             // this displays the count of low-stock items

        // linking the RecyclerView to show recent sales
        rvRecentSales = findViewById(R.id.rv_recent_sales);

        // setting the layout manager for RecyclerView
        // the LinearLayoutManager arranges items vertically, like a list
        rvRecentSales.setLayoutManager(new LinearLayoutManager(this));
    }

    // creatign a method to handle the bottom navigation bar setup and item selection (Risner, 2017)
    private void setupBottomNavigation() {
        // finding the BottomNavigationView by its ID
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // marking "Dashboard" as the selected item in the navigation bar
        bottomNav.setSelectedItemId(R.id.nav_dashboard);

        // creating an event listener for when a navigation item is selected
        bottomNav.setOnItemSelectedListener(item -> {
            // getting the ID of the selected menu item
            int itemId = item.getItemId();

            // checking which navigation item was clicked and then it will  open the appropriate activity
            if (itemId == R.id.nav_inventory) {
                // navigating to InventoryActivity screen (Risner, 2017)
                startActivity(new Intent(this, InventoryActivity.class));
                // disabling the animation between transitions
                overridePendingTransition(0, 0);
                // closing the current activity so the new one becomes active
                finish();
                return true;

            } else if (itemId == R.id.nav_sales) {
                // navigating to SalesActivity screen
                startActivity(new Intent(this, SalesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_reports) {
                // navigating to the ReportsActivity screen
                startActivity(new Intent(this, ReportsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            // returning false if none of the above options were selected
            return false;
        });
    }

    // creating a method to load and display real-time dashboard data from the database
    private void loadDashboardData() {
        // displaying the total number of products by querying the database
        tvTotalProducts.setText(String.valueOf(dbHelper.getTotalProducts()));

        // displaying the total sales formatted as currency (Slingacademy.com, 2024)
        tvTotalSales.setText(currencyFormat.format(dbHelper.getTotalSales()));

        // displaying the total inventory value formatted as currency
        tvInventoryValue.setText(currencyFormat.format(dbHelper.getInventoryValue()));

        // displaying the number of products that are low in stock
        tvLowStock.setText(String.valueOf(dbHelper.getLowStockProducts().size()));

        // getting  all the sales records from the database
        List<Sale> recentSales = dbHelper.getAllSales();

        // limiting the recent sales list to only the first 5 entries (most recent ones)
        if (recentSales.size() > 5) {
            recentSales = recentSales.subList(0, 5);
        }

        // creating an adapter to bind the recent sales list to the RecyclerView (Abhiandroid.com, 2019)
        SalesAdapter adapter = new SalesAdapter(this, recentSales);

        // attaching  the adapter to the RecyclerView so the sales appear in the list (Abhiandroid.com, 2019)
        rvRecentSales.setAdapter(adapter);
    }

    // onResume() is called when the user returns to this activity
    // This ensures the dashboard refreshes with updated data each time it’s reopened
    @Override
    protected void onResume() {
        super.onResume();
        // Reload dashboard data to ensure displayed values are up to date
        loadDashboardData();
    }
}
