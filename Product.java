package com.businesspro.inventorymanager;

//the Product class represents a single product in the inventory system.
//it also acts as a data model or "blueprint" for creating and managing product objects.
public class Product {

    //creating a unique identifier for each product (it will be used as a primary key in the database)
    private int id;

    //the name of the product ("Laptop")
    private String name;

    //the selling price of the product
    private double price;

    //the current quantity of the product in stock
    private int stock;

    //the category the product belongs to (like "Electronics")
    private String category;

    //the cost price (how much the business pays to get the product)
    private double cost;

    //creating a constructor to initialize a Product object with all its attributes
    public Product(int id, String name, double price, int stock, String category, double cost) {
        //assigning the given ID to the product
        this.id = id;

        //assigning the name passed by the user to the product’s name field
        this.name = name;

        //assigning the selling price of the product
        this.price = price;

        //assigning how many items of this product are currently available in stock
        this.stock = stock;

        //assigning the product’s category (for sorting or filtering)
        this.category = category;

        //assigning the cost price for calculating profit margins later
        this.cost = cost;
    }

    //creating the getter method that returns the product ID
    public int getId() {
        return id;
    }

    //creating the getter method that returns the product name
    public String getName() {
        return name;
    }

    //creating the getter method that returns the selling price of the product
    public double getPrice() {
        return price;
    }

    //creating the getter method that returns how many units are currently in stock
    public int getStock() {
        return stock;
    }

    //creating the getter method that  returns the product category (it's useful for grouping or filtering)
    public String getCategory() {
        return category;
    }

    //creating the getter method that returns the cost price (used in profit calculations)
    public double getCost() {
        return cost;
    }

    //creating a method to check if the product has low stock.
    //it returns true if stock is less than or equal to 10, indicating a low stock warning.
    public boolean isLowStock() {
        return stock <= 10;
    }
}
