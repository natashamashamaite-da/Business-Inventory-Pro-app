package com.businesspro.inventorymanager;

//importing necessary android and java libraries
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//this  class represents the "Reports & Analytics" section of the inventory management app.
//it allows users to view total revenue, profit, profit margins, low stock alerts, and export sales data to a CSV file.
public class ReportsActivity extends AppCompatActivity {

    //requesting the code used when asking for permissions (used on Android 9 and below)
    private static final int PERMISSION_REQUEST_CODE = 100;

    //declaring the database helper that manages database operations
    private DatabaseHelper dbHelper;

    //declaring TextView UI elements for displaying report data
    private TextView tvTotalRevenue, tvTotalProfit, tvProfitMargin, tvLowStockReport;

    //creating a formatter to format numbers into the South African Rand currency format
    private NumberFormat currencyFormat;

    //calling onCreate() for when the activity is first launched.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting the layout for this activity
        setContentView(R.layout.activity_reports);
        //setting the action bar title
        getSupportActionBar().setTitle("Reports & Analytics");

        //initialising the database helper for performing SQL operations
        dbHelper = new DatabaseHelper(this);

        //setting up currency formatting for ZAR
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));

        //initializing all TextView components
        initViews();
        //setting up the navigation menu at the bottom
        setupBottomNavigation();
        //loading the financial data and stock reports
        loadReports();

        //finding the export button from the layout and attach an event listener
        Button btnExport = findViewById(R.id.btn_export_csv);
        btnExport.setOnClickListener(v -> exportToCSV());
    }

    //initialising  all the TextView UI components
    private void initViews() {
        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        tvTotalProfit = findViewById(R.id.tv_total_profit);
        tvProfitMargin = findViewById(R.id.tv_profit_margin);
        tvLowStockReport = findViewById(R.id.tv_low_stock_report);
    }

    //configuring the bottom navigation bar and handling the  user navigation between app sections
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_reports); //highlights the current tab (Reports)

        //defining the actions for each navigation item
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class)); //allows users to navigate to Dashboard
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_inventory) {
                startActivity(new Intent(this, InventoryActivity.class)); //allows users to navigate to Inventory
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_sales) {
                startActivity(new Intent(this, SalesActivity.class)); //allows users to navigate to Sales screen
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false; //default case: do nothing
        });
    }

    //loading report data such as total revenue, total profit, profit margin, and low stock products
    private void loadReports() {
        // Get data from the database
        double totalSales = dbHelper.getTotalSales();   //gives the total revenue from sales
        double totalProfit = dbHelper.getTotalProfit(); //gives the total profit calculated from cost vs. price
        double profitMargin = totalSales > 0 ? (totalProfit / totalSales) * 100 : 0; //gives the profit margin in %

        //displaying the formatted financial data in TextViews
        tvTotalRevenue.setText(currencyFormat.format(totalSales));
        tvTotalProfit.setText(currencyFormat.format(totalProfit));
        tvProfitMargin.setText(String.format(Locale.getDefault(), "%.2f%%", profitMargin));

        //getting teh list of products with low stock from the database
        List<Product> lowStockProducts = dbHelper.getLowStockProducts();
        StringBuilder lowStockReport = new StringBuilder();

        //if no low stock products exist, it will show default message
        if (lowStockProducts.isEmpty()) {
            lowStockReport.append("No low stock items");
        } else {
            //otherwise, it will list all low stock products with their remaining stock
            for (Product p : lowStockProducts) {
                lowStockReport.append("• ").append(p.getName())
                        .append(" - ").append(p.getStock()).append(" units\n");
            }
        }

        //displaying the low stock report on screen
        tvLowStockReport.setText(lowStockReport.toString());
    }

    //exporting all sales data into a csv file
    private void exportToCSV() {
        try {
            File exportDir;

            //choosing the export directory depending on my android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //for Android 10+ (Scoped Storage), it will use app-specific directory
                exportDir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "InventoryReports");
            } else {
                //then for Android 9 and below it will use Downloads folder ( and it needs permission thats why I created file_paths.xml)
                exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "InventoryReports");
            }

            //creating a directory if it doesn’t exist
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            // Create a timestamped filename (e.g., sales_report_20251103_103000.csv)
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File file = new File(exportDir, "sales_report_" + timeStamp + ".csv");

            //creating a FileWriter to write csv data into the file
            FileWriter writer = new FileWriter(file);

            //writing a csv header (column names)
            writer.append("Sale ID,Product Name,Quantity,Price,Total,Date,Profit\n");

            //getting all sales data from the database
            List<Sale> sales = dbHelper.getAllSales();

            //using a for loop to loop through all sales and write them as csv rows
            for (Sale sale : sales) {
                writer.append(String.valueOf(sale.getSaleId())).append(",");
                writer.append("\"").append(sale.getProductName()).append("\"").append(",");
                writer.append(String.valueOf(sale.getQuantity())).append(",");
                writer.append(String.valueOf(sale.getSalePrice())).append(",");
                writer.append(String.valueOf(sale.getTotal())).append(",");
                writer.append("\"").append(sale.getDate()).append("\"").append(",");
                writer.append(String.valueOf(sale.getProfit())).append("\n");
            }

            //saving and close file
            writer.flush();
            writer.close();

            //it will show the success dialog with the file name and path
            String message = "Report exported successfully!\n\nFile: " + file.getName() + "\nLocation: " + exportDir.getAbsolutePath();

            new android.app.AlertDialog.Builder(this)
                    .setTitle("Export Successful")
                    .setMessage(message)
                    .setPositiveButton("Open Folder", (dialog, which) -> openFileLocation(file))
                    .setNegativeButton("OK", null)
                    .show();

        } catch (IOException e) {
            //handling file writing errors
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    //opens the folder where the exported csv file is stored
    private void openFileLocation(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri;

            //for Android 7.0+ , it will use FileProvider to safely expose file paths
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider",
                        file.getParentFile());
            } else {
                //fo r older Android versions, it will use direct file URI
                uri = Uri.fromFile(file.getParentFile());
            }

            //telling Android we want to open a folder (not a single file)
            intent.setDataAndType(uri, "resource/folder");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            //attempting to open the folder with a file manager app
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Open folder with"));
            } else {
                //if no file manager available, show file path instead
                Toast.makeText(this, "File saved at: " + file.getParentFile().getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            //in case of an unexpected error, still show the file location
            Toast.makeText(this, "File saved at: " + file.getParentFile().getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
    }
}
