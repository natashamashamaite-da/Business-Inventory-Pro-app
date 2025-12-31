package com.businesspro.inventorymanager;

//importing necessary android and java libraries used in this class
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

//this activity handles recording a sale transaction for a selected product.
//it allows the user to select a product, enter a quantity sold, and updates the database accordingly.
public class RecordSaleActivity extends AppCompatActivity {

    //declaring the UI components
    private Spinner spProduct; //this is for the dropdown spinner for selecting a product
    private EditText etQuantity; //this will be the input field for quantity sold

    //declaring helper and data variables
    private DatabaseHelper dbHelper; //this will be used for interacting with the database
    private List<Product> products;  //this stores all products loaded from the database

    //calling the onCreate() method is the entry point when the activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //calling the parent class method
        setContentView(R.layout.activity_record_sale); //loading the XML layout file for this screen

        //setting up the action bar with a title and a back button
        getSupportActionBar().setTitle("Record Sale");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialising the database helper to perform database operations
        dbHelper = new DatabaseHelper(this);

        //linking the java variables to xml UI elements using their IDs (sp_product)
        spProduct = findViewById(R.id.sp_product);
        etQuantity = findViewById(R.id.et_quantity);

        //loading all products from the database into the spinner
        loadProducts();

        //setting up the button and define what happens when clicked
        Button btnRecord = findViewById(R.id.btn_record_sale);
        btnRecord.setOnClickListener(v -> recordSale()); //when clicked, it will record the sale
    }

    //loading all products from the database and displays them in the spinner dropdown
    private void loadProducts() {
        products = dbHelper.getAllProducts(); //fetching all products from the database

        //creating a list of product names (with stock info) to display in the dropdown
        List<String> productNames = new ArrayList<>();
        for (Product p : products) {
            productNames.add(p.getName() + " (Stock: " + p.getStock() + ")");
        }

        //creating  an ArrayAdapter to connect the product list to the spinner UI
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, //context (this activity)
                android.R.layout.simple_spinner_item, //using a built-in simple layout for spinner items
                productNames //the data to show
        );

        //setting the layout for the dropdown view when the spinner is opened
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching the adapter to the spinner
        spProduct.setAdapter(adapter);
    }

    //handling recording a sale and updating the database accordingly
    private void recordSale() {
        //checking if there are any products available in the system
        if (products.isEmpty()) {
            Toast.makeText(this, "No products available", Toast.LENGTH_SHORT).show();
            return;
        }

        //getting the entered quantity value as text and remove spaces
        String quantityStr = etQuantity.getText().toString().trim();

        // If the quantity field is empty, it will show an error message
        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            //changing the entered text into an integer quantity
            int quantity = Integer.parseInt(quantityStr);

            //getting the product that the user selected from the spinner
            Product selectedProduct = products.get(spProduct.getSelectedItemPosition());

            //ensuring the quantity is a positive number
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            //checking that the requested sale quantity does not exceed available stock
            if (quantity > selectedProduct.getStock()) {
                Toast.makeText(
                        this,
                        "Insufficient stock! Available: " + selectedProduct.getStock(),
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            //recording the sale in the database
            //parameters will be : product ID, name, quantity, price, and cost
            long result = dbHelper.recordSale(
                    selectedProduct.getId(),
                    selectedProduct.getName(),
                    quantity,
                    selectedProduct.getPrice(),
                    selectedProduct.getCost()
            );

            //if the database insert was successful (result > 0)
            if (result > 0) {
                Toast.makeText(this, "Sale recorded successfully", Toast.LENGTH_SHORT).show();

                //retrieving the updated product to check its new stock level
                Product updatedProduct = dbHelper.getProduct(selectedProduct.getId());

                //if the stock is low after the sale, it will  alert the user
                if (updatedProduct.isLowStock()) {
                    Toast.makeText(
                            this,
                            "WARNING: Low stock alert for " + updatedProduct.getName() +
                                    "! Only " + updatedProduct.getStock() + " remaining.",
                            Toast.LENGTH_LONG
                    ).show();
                }

                //closing the activity and returning to the previous screen
                finish();
            } else {
                //showing a message if database insertion failed
                Toast.makeText(this, "Failed to record sale", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            //if the entered quantity is not a valid number
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
        }
    }

    //allows the back button (arrow in the top bar) to return to the previous screen
    @Override
    public boolean onSupportNavigateUp() {
        finish(); //closing this activity
        return true; //returns true to indicate the navigation was handled
    }
}
