package com.businesspro.inventorymanager;

//importing statements for android and java libraries used in this class
import android.content.Context; //this provides access to app resources and environment
import android.view.LayoutInflater; //this is used to convert xml layouts into View objects
import android.view.View; //this represents a single UI element in Android
import android.view.ViewGroup; //this is a container for other UI elements
import android.widget.TextView; //this is a UI element to display text
import androidx.annotation.NonNull; //this is an annotation indicating non-null parameters/returns
import androidx.recyclerview.widget.RecyclerView; //recyclerview manages and recycles list item views efficiently
import java.text.NumberFormat; //this formats numbers as currency
import java.text.ParseException; //this handles errors during date parsing
import java.text.SimpleDateFormat; //this formats and parses dates
import java.util.Date; //this represents a specific moment in time
import java.util.List; //this creates a generic list interface for collections
import java.util.Locale; //this represents geographic and cultural settings (like language or currency)

//the Adapter class that connects the sales data (List<Sale>) with the RecyclerView UI
public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SaleViewHolder> {

    //context gives access to app resources and activities
    private Context context;

    //creating a list that holds all sales records retrieved from the database
    private List<Sale> sales;

    //creating a formatter to display prices and profit in South African currency format
    private NumberFormat currencyFormat;

    //input date format is used to interpret the stored date in the database
    private SimpleDateFormat inputFormat;

    //output date format is used to display human-friendly formatted date in the UI
    private SimpleDateFormat outputFormat;

    //creating a constructor initializes variables when a SalesAdapter object is created
    public SalesAdapter(Context context, List<Sale> sales) {
        this.context = context; //store the context (usually an Activity)
        this.sales = sales; //store the list of sales to be displayed

        //initialising a currency formatter for South Africa (ZAR)
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));

        //creating a format of how the date is stored in the database (e.g., 2025-11-03 10:45:00)
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        //adding the format of how the date will appear in the RecyclerView (e.g., 03 Nov 2025, 10:45)
        this.outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    }

    //calling onCreateViewHolder when RecyclerView needs a new ViewHolder (a new list item view)
    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the xml layout file (item_sale.xml) into a View object
        View view = LayoutInflater.from(context).inflate(R.layout.item_sale, parent, false);

        //creating and returning a new ViewHolder to hold this view
        return new SaleViewHolder(view);
    }

    //calling onBindViewHolder is called when RecyclerView binds (attaches) data to a specific list item
    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
        // Get the Sale object at the current position
        Sale sale = sales.get(position);

        //setting the product name into the TextView
        holder.tvProductName.setText(sale.getProductName());

        //displaying the quantity sold with a label
        holder.tvQuantity.setText("Qty: " + sale.getQuantity());

        //formatting and displaying the total sale amount as currency
        holder.tvTotal.setText(currencyFormat.format(sale.getTotal()));

        //formatting and displaying the profit as currency
        holder.tvProfit.setText("Profit: " + currencyFormat.format(sale.getProfit()));

        try {
            //using a try-catch block to convert the date string from database format to a Date object
            Date date = inputFormat.parse(sale.getDate());

            //if the date conversion was successful, format it for display
            if (date != null) {
                holder.tvDate.setText(outputFormat.format(date));
            }
            //if the date is null, just display the raw string
            else {
                holder.tvDate.setText(sale.getDate());
            }

            //if there’s an error while parsing (invalid format), show the raw date string instead
        } catch (ParseException e) {
            holder.tvDate.setText(sale.getDate());
        }
    }

    //returns how many items are in the list (used by RecyclerView)
    @Override
    public int getItemCount() {
        return sales.size();
    }

    //adding an inner class that holds all UI elements for a single sale item
    static class SaleViewHolder extends RecyclerView.ViewHolder {

        //declaring TextViews for each piece of sale data displayed
        TextView tvProductName, tvQuantity, tvTotal, tvDate, tvProfit;

        //creating a constructor that finds the TextViews in the XML layout
        SaleViewHolder(View itemView) {
            super(itemView); //passing the view to the superclass (RecyclerView.ViewHolder)

            //connecting java objects to the corresponding TextViews in item_sale.xml
            tvProductName = itemView.findViewById(R.id.tv_sale_product_name);
            tvQuantity = itemView.findViewById(R.id.tv_sale_quantity);
            tvTotal = itemView.findViewById(R.id.tv_sale_total);
            tvDate = itemView.findViewById(R.id.tv_sale_date);
            tvProfit = itemView.findViewById(R.id.tv_sale_profit);
        }
    }
}
