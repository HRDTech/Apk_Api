package com.solucioneshr.apk_test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.solucioneshr.apk_test.controller.ApiUtils;
import com.solucioneshr.apk_test.controller.IntRetrofit;
import com.solucioneshr.apk_test.databinding.ActivityMainBinding;
import com.solucioneshr.apk_test.model.ActEvent;
import com.solucioneshr.apk_test.model.CategoryAdapter;
import com.solucioneshr.apk_test.model.EventMessage;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private static ActivityMainBinding binding;
    private NavController navController;
    private boolean fragmentFirst = true;
    private static ArrayList<String> category;
    public static View.OnClickListener Click_Category;
    private static RecyclerView recyclerViewCategory;
    private ProgressDialog progressDialog;
    private static AlertDialog dialogCategory;
    private boolean searchOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            progressDialog = ProgressDialog.show(this, "Apk Test", "Cargando datos..", true, false);

            Click_Category = new MyOnClickListener(this);

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            category = new ArrayList<>();
            IntRetrofit getCategory = ApiUtils.getListCategory();
            getCategory.Get_Category().enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    if (response.body() != null && response != null) {
                        if (fragmentFirst) {
                            category.add("random");
                            category.addAll(response.body());
                            progressDialog.dismiss();
                        } else {
                            category.addAll(response.body());
                            progressDialog.dismiss();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    Toast.makeText(getParent(), t.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

            binding.toolbar.setTitle("Apk_Test Category: random");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_category){
            dialogCategory = Create_SelectCategory();
            dialogCategory.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogCategory.show();
        }

        if(id == R.id.action_search){
            if (searchOpen){
                EventBus.getDefault().post(new ActEvent(false));
                searchOpen = false;
            } else {
                EventBus.getDefault().post(new ActEvent(true));
                searchOpen = true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private AlertDialog Create_SelectCategory (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View theView = inflater.inflate(R.layout.layout_category, null);
        builder.setView(theView);

        recyclerViewCategory = theView.findViewById(R.id.recyclerViewDialogCategory);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewCategory.setLayoutManager(layoutManager);
        recyclerViewCategory.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.Adapter adapter = new CategoryAdapter(category);
        recyclerViewCategory.setAdapter(adapter);


        return builder.create();
    }

    private static class MyOnClickListener implements View.OnClickListener{
        private final Context context;

        public MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(new EventMessage(category.get(recyclerViewCategory.getChildPosition(v))));
            //Toast.makeText(context, category.get(recyclerViewCategory.getChildPosition(v)), Toast.LENGTH_LONG).show();
            binding.toolbar.setTitle("Apk_Test Category: " + category.get(recyclerViewCategory.getChildPosition(v)));
            dialogCategory.dismiss();
        }
    }

}