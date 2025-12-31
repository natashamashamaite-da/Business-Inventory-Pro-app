package com.businesspro.inventorymanager;

// importing required Android and app libraries
import android.app.AlertDialog; //this is used to create confirmation dialogs (for delete confirmation)
import android.content.Intent; // this allows switching between activities (navigation)
import android.os.Bundle; //this is used for saving/restoring activity state
import android.widget.Toast; //this displays short popup messages to give feedback to the user

import androidx.appcompat.app.AppCompatActivity; //this is creates the base class for activities using modern ActionBar features
import androidx.recyclerview.widget.LinearLayoutManager; //this lays out items in a vertical list for RecyclerView
import androidx.recyclerview.widget.RecyclerView; //this displays scrollable lists of data efficiently
//Provides bottom navigation bar for easy screen switching
import com.google.android.material.bottomnavigation.BottomNavigationView;
//this adds the Circular button for adding new items quickly
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List; //this is used for handling lists of Product objects

//the InventoryActivity displays all products and allows adding, editing, and deleting products
//it also includes navigation between other activities (Dashboard, Sales, Reports)
public class InventoryActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    //creating the database helper instance to interact with the SQLite database
    private DatabaseHelper dbHelper;

    //creating the RecyclerView for displaying the list of products
    private RecyclerView rvProducts;

    //creating the adapter for binding data to RecyclerView
    private ProductAdapter adapter;

    //creating the onCreate() method that's called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //it calls parent method to initialize activity
        setContentView(R.layout.activity_inventory); //this sets the xml layout for this activity

        //sets the title displayed in the app bar
        getSupportActionBar().setTitle("Inventory Management");

        //initialising the database helper to perform CRUD operations
        dbHelper = new DatabaseHelper(this);

        //using findView to find RecyclerView defined in xml and link it to java variable
        rvProducts = findViewById(R.id.rv_products);

        //setting the layout manager for RecyclerView — determines how items are arranged (here vertically)
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        //using findView to find the floating action button used to add new products
        FloatingActionButton fab = findViewById(R.id.fab_add_product);

        //setting a click listener on the floating action button
        //when clicked, it starts the AddProductActivity to create a new product
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddProductActivity.class)));

        //setting up bottom navigation bar for switching between main app sections
        setupBottomNavigation();

        //load all product data from database and display it
        loadProducts();
    }

    //setting up the bottom navigation bar functionality
    private void setupBottomNavigation() {
        //using findView to find the BottomNavigationView from layout
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        //highlighting the "Inventory" item as currently selected
        bottomNav.setSelectedItemId(R.id.nav_inventory);

        //setting up navigation item selection listener to handle clicks
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get ID of clicked item

            //if the Dashboard icon is clicked
            if (itemId == R.id.nav_dashboard) {
                //allowing the user to navigate to DashboardActivity
                startActivity(new Intent(this, DashboardActivity.class));
                //overriding the animation (set to 0 to make transition instant)
                overridePendingTransition(0, 0);
                //finishing the current activity to avoid stacking activities
                finish();
                return true;
            }
            //if the sales icon is clicked
            else if (itemId == R.id.nav_sales) {
                //navigate to SalesActivity
                startActivity(new Intent(this, SalesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            //if the reports icon is clicked
            else if (itemId == R.id.nav_reports) {
                // Navigate to ReportsActivity
                startActivity(new Intent(this, ReportsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false; // If no match was  found, do nothing
        });
    }

    // creating a method to load all products from the database and displays them in the RecyclerView
    private void loadProducts() {
        //retrieving the  list of all products from the database
        List<Product> products = dbHelper.getAllProducts();

        //creating a ProductAdapter instance to bind the product data to the RecyclerView
        adapter = new ProductAdapter(this, products, this);

        //setting the adapter to the RecyclerView so that it displays the list
        rvProducts.setAdapter(adapter);
    }

    //calling onEditClick for when the user clicks the Edit button on a product item
    @Override
    public void onEditClick(Product product) {
        //creating an intent to open EditProductActivity
        Intent intent = new Intent(this, EditProductActivity.class);

        //passing the selected product’s ID so that the edit screen knows which product to edit
        intent.putExtra("product_id", product.getId());

        //starting the EditProductActivity
        startActivity(intent);
    }

    //calling onDeleteClick for when the user clicks the Delete button on a product item
    @Override
    public void onDeleteClick(Product product) {
        //it will show a confirmation dialog before deleting the product
        new AlertDialog.Builder(this)
                .setTitle("Delete Product") // Set dialog title
                .setMessage("Are you sure you want to delete " + product.getName() + "?") // Ask for confirmation
                //positive button — if the  user confirms deletion
                .setPositiveButton("Delete", (dialog, which) -> {
                    //deleting the  product from database
                    dbHelper.deleteProduct(product.getId());

                    //reloading the product list to update display
                    loadProducts();

                    //showing the confirmation toast
                    Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
                })
                //negative button — dismisses dialog without deleting
                .setNegativeButton("Cancel", null)
                //dsplaying the dialog on screen
                .show();
    }

    //calling onResume() when returning to this activity (after editing or adding a product)
    @Override
    protected void onResume() {
        super.onResume();
        //refreshing the  product list to reflect any new or updated data
        loadProducts();
    }
}
