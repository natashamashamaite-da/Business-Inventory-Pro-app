// The package name defines the  namespace for the app
package com.businesspro.inventorymanager;

// importing all the necessary android classes for user interface and app functionality
import android.os.Bundle; // this is used for saving and restoring activity state
import android.widget.ArrayAdapter; // this will be used to bind arrays to spinner UI
import android.widget.Button; // this is for clickable button components
import android.widget.EditText; // this is for text input fields
import android.widget.Spinner; // this is for dropdown list UI element
import android.widget.Toast; // this is for displaying small popup messages

// importing AppCompatActivity to ensure compatibility with modern android versions
import androidx.appcompat.app.AppCompatActivity;

// Creating the main class that handles adding a new product,
// tt extends AppCompatActivity, which provides lifecycle and ui  management methods
public class AddProductActivity extends AppCompatActivity {

    // declaring EditText variables for product details input fields
    // and these are private because they are only accessed within this class and nowhere else
    private EditText etName, etPrice, etStock, etCost;

    // the spinner will allow the user to choose a category for the product
    private Spinner spCategory;

    // creating a databaseHelper object to manage all database operations (inserting, updating)
    private DatabaseHelper dbHelper;

    // calling the onCreate() method when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // calling the superclass's onCreate method to properly initialize the activity
        super.onCreate(savedInstanceState);

        // setting the layout xml  file that defines this activity's user interface
        setContentView(R.layout.activity_add_product);

        // setting the title of the action bar to show that this screen is for adding a new product
        getSupportActionBar().setTitle("Add New Product");

        // enabling the back arrow in the action bar for easy navigation back to the previous screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the database helper object so we can interact with our SQLite database
        dbHelper = new DatabaseHelper(this);

        // connecting (binding) the  variables to the corresponding views in the layout file by their id (Devdoc.net, 2024)
        etName = findViewById(R.id.et_product_name);  // this is teh product name input field
        etPrice = findViewById(R.id.et_price);        // this is the product price input field
        etStock = findViewById(R.id.et_stock);        // this is the product stock quantity input field
        etCost = findViewById(R.id.et_cost);          // this is the product cost input field
        spCategory = findViewById(R.id.sp_category);  // this is the spinner for selecting the  product category (Devdoc.net, 2024)

        // creating an array of product categories that will appear in the spinner dropdown
        String[] categories = {"Electronics", "Accessories", "Furniture", "Clothing", "Food", "Other"};

        // Creating an ArrayAdapter to bind the category list to the spinner (Abhiandroid.com, 2019)
        // 'android.R.layout.simple_spinner_item' defines the default layout for each item in the dropdown (Abhiandroid.com, 2019)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        // setting the layout used when the spinner dropdown expands (Codingtechroom.com, 2025)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching the adapter to the spinner to display the category options
        spCategory.setAdapter(adapter);

        // finding the save button by its id in the layout
        Button btnSave = findViewById(R.id.btn_save);

        // setting an OnClickListener to handle what happens when the user clicks the save button and,
        // when it's clicked, it will call the saveProduct() method
        btnSave.setOnClickListener(v -> saveProduct());
    }

    // creating a method to handle the process of saving a product to the database
    private void saveProduct() {
        // retrieving the  user input values from the EditText fields and then trimming extra spaces
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String costStr = etCost.getText().toString().trim();

        // retrieving the selected category from the spinner
        String category = spCategory.getSelectedItem().toString();

        // validating that no fields are empty before saving
        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || costStr.isEmpty()) {
            // displaying a message that will prompt the user to fill in all required fields
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return; // exitting the method early if validation fails
        }

        // using  a try-catch block to handle invalid number formats (e.g., if user types letters instead of numbers)
        try {
            // changing the  string inputs into their numeric data types
            double price = Double.parseDouble(priceStr); // changing price to double
            int stock = Integer.parseInt(stockStr);      // changing  stock quantity to integer
            double cost = Double.parseDouble(costStr);   // changing cost to double

            // validating that the numbers entered make sense (no negative or zero values for price)
            if (price <= 0 || stock < 0 || cost < 0) {
                Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show();
                return; // stopping the  execution if any given values are invalid
            }

            // calling the database helper to insert the product into the sqlite database (Anon, 2019)
            // The method  will return a long value indicating success (>0 means success)
            long result = dbHelper.addProduct(name, price, stock, category, cost);

            // if the insertion was successful, show a success message and close the activity (Anon, 2019)
            if (result > 0) {
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                finish(); // closing the current screen and return to the previous one
            } else {
                // if the  insertion failed, show an error message (Anon, 2019)
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            // catching an exception if the user entered invalid number formats
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    // handling  what happens when the user presses the back arrow in the action bar
    @Override
    public boolean onSupportNavigateUp() {
        // closing this activity and return to the previous screen
        finish();
        return true; // returning true to show that the event was handled
    }
}
