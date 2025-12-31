package com.businesspro.inventorymanager;

//the 'Sale' class represents a single sales transaction in the inventory system.
//each Sale object stores details about one specific sale: which product was sold,
//how many units were sold, when it happened, and how much profit was made.
public class Sale {

    //creating a unique identifier for each sale (Primary Key in the database)
    private int saleId;

    //the ID of the product that was sold (foreign key referencing the Product table)
    private int productId;

    //the name of the product sold, it's  stored here for quick access in reports
    private String productName;

    //number of units sold in this transaction
    private int quantity;

    //price per unit at which the product was sold
    private double salePrice;

    //total amount of money made from this sale (quantity * salePrice)
    private double total;

    //date when this sale occurred, usually stored in a human-readable format
    private String date;

    //profit earned from this sale ( (salePrice - cost) * quantity )
    private double profit;

    //creating a constructor that initializes all fields of the Sale object when creating a new instance.
    //it's called whenever a sale is recorded or retrieved from the database.
    public Sale(int saleId, int productId, String productName, int quantity,
                double salePrice, double total, String date, double profit) {

        //assigning parameter values to class fields
        this.saleId = saleId;             //set sale ID
        this.productId = productId;       //set product ID reference
        this.productName = productName;   //set product name
        this.quantity = quantity;         //set quantity sold
        this.salePrice = salePrice;       //set unit price
        this.total = total;               //set total sale amount
        this.date = date;                 //set date of sale
        this.profit = profit;             //set profit earned
    }

    //adding getter methods to provide read-only access to private fields.
    //these methods are used by adapters, reports, and database handlers to access sale details.

    //returns the unique sale ID
    public int getSaleId() { return saleId; }

    //returns the product ID associated with this sale
    public int getProductId() { return productId; }

    //returns the name of the product that was sold
    public String getProductName() { return productName; }

    //returns how many units were sold in this transaction
    public int getQuantity() { return quantity; }

    //returns the price per unit at which the product was sold
    public double getSalePrice() { return salePrice; }

    //returns the total amount earned from this sale
    public double getTotal() { return total; }

    //returns the date the sale occurred
    public String getDate() { return date; }

    //returns the total profit earned from this sale
    public double getProfit() { return profit; }
}
