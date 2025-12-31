package com.businesspro.inventorymanager;

//importing necessary android and java classes used in this adapter (Medium.com, 2025)
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

//the ProductAdapter is a custom adapter for displaying product data inside a RecyclerView.
//it connects (binds) the Product model data to the UI layout (item_product.xml) for each product item.
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    //creating a context object to access app resources and inflate layouts
    private Context context;

    //creating a list of Product objects that will be displayed in the RecyclerView
    private List<Product> products;

    //adding the interface reference to handle button clicks (edit and delete)
    private OnProductClickListener listener;

    //creating a NumberFormat object to format prices as South African currency (R)
    private NumberFormat currencyFormat;

    //the interface will  defines click event methods for edit and delete actions
    public interface OnProductClickListener {
        void onEditClick(Product product);    //this is called when edit button is clicked
        void onDeleteClick(Product product);  //this is called when delete button is clicked
    }

    //creating a constructor to initialize the adapter with a context, list of products, and click listener
    public ProductAdapter(Context context, List<Product> products, OnProductClickListener listener) {
        this.context = context;          //Saving the context for later use (e.g., inflating layouts)
        this.products = products;        //storing the list of products to display
        this.listener = listener;        //assigning the listener that handles user clicks
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "ZA")); // Formats prices in ZAR currency (e.g., R150.00)
    }

    //the ProductViewHolder is called by RecyclerView when it needs a new view to display an item.
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //it inflates the xml layout for a single product row (item_product.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);

        //it returns a new ViewHolder instance that holds references to the layout's views
        return new ProductViewHolder(view);
    }

    //calling the onBindViewHolder to bind data from a Product object to a ViewHolder (one item in the list)
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        //getting the current product object based on its position in the list
        Product product = products.get(position);

        //setting the product name text
        holder.tvName.setText(product.getName());

        //formatting and displaying the product’s price as South African currency
        holder.tvPrice.setText(currencyFormat.format(product.getPrice()));

        //displaying the  current stock level
        holder.tvStock.setText("Stock: " + product.getStock());

        //displaying the product category (e.g., Electronics, Food, etc.)
        holder.tvCategory.setText(product.getCategory());

        //checking if the product has low stock (≤ 10)
        if (product.isLowStock()) {
            //changing the text color to a warning color (defined in colors.xml as R.color.low_stock)
            holder.tvStock.setTextColor(context.getResources().getColor(R.color.low_stock));

            //adding a warning emoji and label the stock as LOW
            holder.tvStock.setText("⚠ Stock: " + product.getStock() + " (LOW)");
        } else {
            //if stock is normal, it will show a success (green) color
            holder.tvStock.setTextColor(context.getResources().getColor(R.color.success));

            //it will then display normal stock message
            holder.tvStock.setText("Stock: " + product.getStock());
        }

        //when the user clicks the edit button, it will call the listener’s edit method
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(product));

        //when the user clicks the delete button, it will call the listener’s delete method
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(product));
    }

    // getItemCount will return the total number of items (products) in the RecyclerView
    @Override
    public int getItemCount() {
        return products.size();
    }

    //The ViewHolder class holds references to all views inside a single RecyclerView item.
    //this improves performance by avoiding repeated findViewById() calls.
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        //declaring all the TextViews and Buttons used in each row layout
        TextView tvName, tvPrice, tvStock, tvCategory;
        ImageButton btnEdit, btnDelete;

        //creating a constructor taht  connects Java variables to the actual views from item_product.xml
        ProductViewHolder(View itemView) {
            super(itemView);
            //assigning  UI components by their IDs defined in item_product.xml
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvStock = itemView.findViewById(R.id.tv_product_stock);
            tvCategory = itemView.findViewById(R.id.tv_product_category);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
