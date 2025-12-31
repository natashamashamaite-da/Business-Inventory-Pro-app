package com.businesspro.inventorymanager;

// importing necessary Android and Java classes (Medium.com, 2025)
import android.os.Bundle; // this is used for saving and restoring activity states
import android.widget.ArrayAdapter; //this is an adapter to populate the Spinner with category options
import android.widget.Button; //this represents the button widget in the UI
import android.widget.EditText; //this is used for user input fields (text entry)
import android.widget.Spinner; //this is for the dropdown list for selecting a category
import android.widget.Toast; //this displays short popup messages for user feedback
import androidx.appcompat.app.AppCompatActivity; //this creates the base class for activities using the modern ActionBar features

// creating a public activity allows the user to edit the details of an existing product in the database
public class EditProductActivity extends AppCompatActivity {

    //declaring input field variables for product attributes
    private EditText etName, etPrice, etStock, etCost;

    //creating the spinner for selecting product category
    private Spinner spCategory;

    //creating the database helper to interact with SQLite database
    private DatabaseHelper dbHelper;

    //creating a variable to hold the product id passed from the previous activity
    private int productId;

    //creating onCreate() method that's called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //this calls the superclass implementation
        setContentView(R.layout.activity_add_product); //reusing the same layout used for adding a product

        //setting the ActionBar title to “Edit Product” so the user knows they are editing an existing product
        getSupportActionBar().setTitle("Edit Product");
        //enabling the Back button (arrow) in the ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialising the database helper for performing CRUD operations
        dbHelper = new DatabaseHelper(this);

        //retrieving the product id passed from another activity (like InventoryActivity)
        productId = getIntent().getIntExtra("product_id", -1); // -1 means invalid/default if not found

        //linking the UI elements in xml layout to the variables in Java
        etName = findViewById(R.id.et_product_name); //this will be the product name input
        etPrice = findViewById(R.id.et_price); //this will be the product price input
        etStock = findViewById(R.id.et_stock); //this will be the product stock input
        etCost = findViewById(R.id.et_cost); //this will be the product cost input
        spCategory = findViewById(R.id.sp_category); //this will be the spinner (dropdown) for categories

        //defining available product categories for the spinner
        String[] categories = {"Electronics", "Accessories", "Furniture", "Clothing", "Food", "Other"};

        //creating an ArrayAdapter to fill the Spinner with the category options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        //setting a dropdown style for the spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //attaching the adapter to the spinner
        spCategory.setAdapter(adapter);

        //loading the existing product data from the database into the input fields for editing
        loadProductData();

        //initialising the Save button and set its click event
        Button btnSave = findViewById(R.id.btn_save);
        //when clicked,the setOnClickListener will trigger the updateProduct() method to save changes to the database
        btnSave.setOnClickListener(v -> updateProduct());
    }

    //using the loadProductData method to retrieve product details from the database using the product ID and fill the UI fields
    private void loadProductData() {
        // Get product data from database by productId
        Product product = dbHelper.getProduct(productId);

        // checking if the product exists
        if (product != null) {
            // filling in the input fields with existing product data
            etName.setText(product.getName());
            etPrice.setText(String.valueOf(product.getPrice()));
            etStock.setText(String.valueOf(product.getStock()));
            etCost.setText(String.valueOf(product.getCost()));

            //getting the category list to set the correct category selection in spinner
            String[] categories = {"Electronics", "Accessories", "Furniture", "Clothing", "Food", "Other"};
            //using a for loop to loop through all categories to find which one matches the product’s category
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(product.getCategory())) {
                    //setting the spinner selection to match the product’s category
                    spCategory.setSelection(i);
                    break; //stop looping once the category is found
                }
            }
        }
    }

    //creating the updateProduct method to update product details in the database
    private void updateProduct() {
        //getting the text values entered by the user
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String costStr = etCost.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString(); // Getting the selected category from spinner

        //validating that no field is left empty
        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || costStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return; //stopping the execution if any field is empty
        }

        try {
            //changing the string inputs into proper numeric types
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);
            double cost = Double.parseDouble(costStr);

            //validating the numerical input values — must be logical (non-negative)
            if (price <= 0 || stock < 0 || cost < 0) {
                Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show();
                return;
            }

            //calling updateProduct() from DatabaseHelper to update product in the sqlite database
            int result = dbHelper.updateProduct(productId, name, price, stock, category, cost);

            //checking if update was successful
            if (result > 0) {
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                finish(); //closing the activity and return to previous screen
            } else {
                Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            //handling incorrect number format (like letters entered in numeric fields)
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    //handling the “back” arrow button in the ActionBar
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Closes the current activity and returns to the previous one
        return true; // Indicate that the navigation event was handled
    }
}
