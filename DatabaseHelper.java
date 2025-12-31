package com.businesspro.inventorymanager;

// importing required Android and Java libraries (Medium.com, 2025)
import android.content.ContentValues; // this is used for inserting and updating key-value pairs in the database
import android.content.Context; // this provides access to application-specific resources and classes
import android.database.Cursor; // this is used to read data from the database query results
import android.database.sqlite.SQLiteDatabase; // this represents the sqlite database itself
import android.database.sqlite.SQLiteOpenHelper; // this is helps manage database creation and version management

// importing date and formatting utilities
import java.text.SimpleDateFormat; // this is used for formatting dates when recording sales
import java.util.ArrayList; // this is used to store lists of Product and Sale objects
import java.util.Date; // this represents current date and time for timestamps
import java.util.List; // this is the interface for holding collections of objects
import java.util.Locale; // this is used for localization (region-specific date formats)

// creating the  DatabaseHelper class that will handle all database operations (create, read, update, delete)
public class DatabaseHelper extends SQLiteOpenHelper {

    // creating the database configuration constants
    private static final String DATABASE_NAME = "InventoryManager.db"; // This is the database file name
    private static final int DATABASE_VERSION = 1; //this is version number (used for upgrades)

    // creating the product table constants (Tutlane.com, 2018)
    private static final String TABLE_PRODUCTS = "products"; //this is the table name for products
    private static final String COL_ID = "id";               //this is the primary key for product
    private static final String COL_NAME = "name";           //this is the product name
    private static final String COL_PRICE = "price";         //this is the selling price of product
    private static final String COL_STOCK = "stock";         //this is the available quantity in stock
    private static final String COL_CATEGORY = "category";   //this is the product category (like electronics)
    private static final String COL_COST = "cost";           //this is the cost price of the product

    // creating the sales table constants (Tutlane.com, 2018)
    private static final String TABLE_SALES = "sales";       //this is the table name for sales
    private static final String COL_SALE_ID = "sale_id";     //this is the unique ID for each sale
    private static final String COL_PRODUCT_ID = "product_id"; //this will link the sale to its product
    private static final String COL_PRODUCT_NAME = "product_name"; //this is the name of product sold
    private static final String COL_QUANTITY = "quantity";         //this is the number of items sold
    private static final String COL_SALE_PRICE = "sale_price";     //this is the selling price per unit
    private static final String COL_TOTAL = "total";               //this is the total sale amount (price * quantity)
    private static final String COL_DATE = "date";                 //this is the date and time of sale
    private static final String COL_PROFIT = "profit";             //this is the profit made on that sale

    // creating a constructor to initialise the database helper and connects to the SQLite database (Unknown, 2025)
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // onCreate is called automatically when the database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL command to create the Products table (STechies, 2017)
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +  //this is the auto-generated product ID
                COL_NAME + " TEXT NOT NULL, " +                    //this is the product name (required)
                COL_PRICE + " REAL NOT NULL, " +                   //this is the product selling price
                COL_STOCK + " INTEGER NOT NULL, " +                //this is the stock quantity
                COL_CATEGORY + " TEXT NOT NULL, " +                //this is the product category
                COL_COST + " REAL DEFAULT 0)";                     //this is the product cost (default 0)

        // SQL command to create the Sales table (STechies, 2017)
        String createSalesTable = "CREATE TABLE " + TABLE_SALES + " (" +
                COL_SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + //this is the auto-generated sale ID
                COL_PRODUCT_ID + " INTEGER, " +                        //this is the product ID (foreign key reference)
                COL_PRODUCT_NAME + " TEXT, " +                         //this is the product name sold
                COL_QUANTITY + " INTEGER, " +                          //this is the quantity sold
                COL_SALE_PRICE + " REAL, " +                           //this is the selling price per item
                COL_TOTAL + " REAL, " +                                //this is the total sale value
                COL_DATE + " TEXT, " +                                 //this is the date of sale
                COL_PROFIT + " REAL)";                                 //this is the profit made

        //now we execute the SQL commands to create both tables (STechies, 2017)
        db.execSQL(createProductsTable);
        db.execSQL(createSalesTable);

        //adding the default sample products into the database for demonstration (STechies, 2017)
        insertSampleData(db);
    }

    //creating a method to insert product data into the database upon creation
    private void insertSampleData(SQLiteDatabase db) {
        //creating  a ContentValues object to store product data for insertion
        ContentValues values = new ContentValues();

        //creating the product information arrays
        String[] products = {"Laptop", "Mouse", "Keyboard", "Monitor", "USB Cable"};
        double[] prices = {15000, 350, 650, 4500, 120};
        int[] stocks = {15, 45, 30, 8, 100};
        String[] categories = {"Electronics", "Accessories", "Accessories", "Electronics", "Accessories"};
        double[] costs = {12000, 200, 400, 3500, 60};

        //using a for loop to loop through all sample data and insert each product into the database (STechies, 2017)
        for (int i = 0; i < products.length; i++) {
            values.clear(); //clearing old values before reusing the object
            values.put(COL_NAME, products[i]);
            values.put(COL_PRICE, prices[i]);
            values.put(COL_STOCK, stocks[i]);
            values.put(COL_CATEGORY, categories[i]);
            values.put(COL_COST, costs[i]);
            db.insert(TABLE_PRODUCTS, null, values); //inserting into products table
        }
    }

    // onUpgrade is called automatically when the database version changes
    // it drops old tables and recreates them (STechies, 2017)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES);
        onCreate(db); //recreating tables
    }

    //crud operations for products
    // Adding a new product record to the database  (Techotopia.com, 2025)
    public long addProduct(String name, double price, int stock, String category, double cost) {
        SQLiteDatabase db = this.getWritableDatabase(); // open writable database
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_PRICE, price);
        values.put(COL_STOCK, stock);
        values.put(COL_CATEGORY, category);
        values.put(COL_COST, cost);
        return db.insert(TABLE_PRODUCTS, null, values); //returning the new row ID or -1 if failed
    }

    // retrieving all the product records from the database  (Techotopia.com, 2025)
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //querying the products table and order results alphabetically by name  (Techotopia.com, 2025)
        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, COL_NAME + " ASC");

        // looping through results and create product objects for each row
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_STOCK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_COST))
                );
                products.add(product); //Add product to list
            } while (cursor.moveToNext());
        }
        cursor.close(); //close cursor after reading
        return products;
    }

    //retrieve a single product record by its ID (Panjuta, 2020)
    public Product getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Product product = null;
        if (cursor.moveToFirst()) {
            product = new Product(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_STOCK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_COST))
            );
        }
        cursor.close();
        return product;
    }

    //updating an existing product record (Panjuta, 2020)
    public int updateProduct(int id, String name, double price, int stock, String category, double cost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_PRICE, price);
        values.put(COL_STOCK, stock);
        values.put(COL_CATEGORY, category);
        values.put(COL_COST, cost);
        // Return number of rows affected
        return db.update(TABLE_PRODUCTS, values, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    //deleting a product by its ID (Panjuta, 2020)
    public void deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    //retrieve all products with stock less than or equal to 10 (low stock) (Panjuta, 2020)
    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COL_STOCK + " <= ?",
                new String[]{"10"}, null, null, COL_STOCK + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_STOCK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_COST))
                );
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    //sales operations:
    //recording a sale and update stock levels (Tutorialspoint.com, 2019)
    public long recordSale(int productId, String productName, int quantity, double salePrice, double cost) {
        SQLiteDatabase db = this.getWritableDatabase();

        //decreasing stock for the sold product (Tutorialspoint.com, 2019)
        db.execSQL("UPDATE " + TABLE_PRODUCTS + " SET " + COL_STOCK + " = " + COL_STOCK + " - ? WHERE " + COL_ID + " = ?",
                new Object[]{quantity, productId});

        //creating a ContentValues object to insert the sale record
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_ID, productId);
        values.put(COL_PRODUCT_NAME, productName);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_SALE_PRICE, salePrice);
        values.put(COL_TOTAL, salePrice * quantity);
        values.put(COL_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        values.put(COL_PROFIT, (salePrice - cost) * quantity); // Calculate profit

        //insert new sale record into the sales table
        return db.insert(TABLE_SALES, null, values);
    }

    //retrieving all recorded sales when the user prompts (w3resource, 2024)
    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SALES, null, null, null, null, null, COL_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Sale sale = new Sale(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SALE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SALE_PRICE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TOTAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROFIT))
                );
                sales.add(sale);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sales;
    }

    //calculating the total sales value from the sales table (SQLite Tutorial, 2022)
    public double getTotalSales() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_TOTAL + ") FROM " + TABLE_SALES, null);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    //calculating the  total profit earned (SQLite Tutorial, 2022)
    public double getTotalProfit() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_PROFIT + ") FROM " + TABLE_SALES, null);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    //calculating the total inventory value (price × stock for all products) (SQLite Tutorial, 2022)
    public double getInventoryValue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_PRICE + " * " + COL_STOCK + ") FROM " + TABLE_PRODUCTS, null);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    //counting the total number of products in the database (SQLite Tutorial, 2022)
    public int getTotalProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}
