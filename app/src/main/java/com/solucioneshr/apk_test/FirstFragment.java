package com.solucioneshr.apk_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.solucioneshr.apk_test.controller.ApiUtils;
import com.solucioneshr.apk_test.controller.IntRetrofit;
import com.solucioneshr.apk_test.databinding.FragmentFirstBinding;
import com.solucioneshr.apk_test.model.ActEvent;
import com.solucioneshr.apk_test.model.DataList;
import com.solucioneshr.apk_test.model.DataRandom;
import com.solucioneshr.apk_test.model.EventMessage;
import com.solucioneshr.apk_test.model.Result;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private String category = "random";
    private ArrayList<Result> arrayData;
    private int cantResult = 0;
    private int contData = 0;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Picasso.get().load(R.drawable.logo).into(binding.imgLogo);

        binding.searchViewFirst.setVisibility(View.INVISIBLE);

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            binding.btnFirstNext.setOnClickListener(this::Click_Next_Data);
            binding.btnFirstBefore.setOnClickListener(this::Click_Before_Data);

            arrayData = new ArrayList<>();
            Load_Data_Random();

            binding.searchViewFirst.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Load_Data(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

            });
        } else {
            binding.textviewFirst.setText("No tiene conexión a Internet ....");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void Load_Data_Random(){
        progressDialog = ProgressDialog.show(getContext(), "Apk Test", "Descargando datos..", true, false);
        IntRetrofit dataRandom = ApiUtils.getDataRandom();
        dataRandom.Get_Data_Random().enqueue(new Callback<DataRandom>() {
            @Override
            public void onResponse(Call<DataRandom> call, Response<DataRandom> response) {
                binding.textviewFirst.setText(response.body().getValue());
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<DataRandom> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void Load_Data (String data){
        progressDialog = ProgressDialog.show(getContext(), "Apk Test", "Descargando datos..", true, false);
        arrayData = new ArrayList<>();
        Call<DataList> listData = ApiUtils.getDataList().Get_Data_List(data);
        listData.enqueue(new Callback<DataList>() {
            @Override
            public void onResponse(Call<DataList> call, Response<DataList> response) {
                if (response.body().getResult().size() > 0) {
                    contData = 0;
                    arrayData.addAll(response.body().getResult());
                    cantResult = response.body().getTotal();
                    binding.textviewFirst.setText(response.body().getResult().get(contData).getValue());
                    binding.textBtnFirstNext.setVisibility(View.VISIBLE);
                    binding.textBtnFirstNext.setText(String.valueOf(cantResult));
                    binding.textBtnFirstBefore.setVisibility(View.INVISIBLE);
                    binding.btnFirstBefore.setVisibility(View.INVISIBLE);
                } else {
                    binding.textviewFirst.setText("La Categoria " + data + " no tiene datos a mostrar....");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<DataList> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        category = event.getText();
        if (category == "random"){
            binding.textBtnFirstBefore.setVisibility(View.INVISIBLE);
            binding.btnFirstBefore.setVisibility(View.INVISIBLE);
            binding.textBtnFirstNext.setVisibility(View.INVISIBLE);
            contData = 0;
            cantResult = 0;
            arrayData = new ArrayList<>();
            Load_Data_Random();
        } else {
            Load_Data(event.getText());
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActEvent event) {
        if (event.isAct()){
            binding.searchViewFirst.setVisibility(View.VISIBLE);
        } else {
            binding.searchViewFirst.setVisibility(View.INVISIBLE);
        }
    };

    public void Click_Next_Data (View v){
        contData ++;

        if (category == "random"){
            if(contData <= cantResult){
                binding.textviewFirst.setText(arrayData.get(contData - 1).getValue());
            } else {
                arrayData.add(new Result(binding.textviewFirst.getText().toString()));
                Load_Data_Random();
                cantResult = contData;
            }

            binding.textBtnFirstNext.setText(String.valueOf(cantResult));
            binding.textBtnFirstBefore.setText(String.valueOf(contData));

        } else {
            binding.textviewFirst.setText(arrayData.get(contData).getValue());
            binding.textBtnFirstNext.setText(String.valueOf(cantResult - contData));
            binding.textBtnFirstBefore.setText(String.valueOf(contData + 1));

            if (contData == (cantResult-1)){
                binding.btnFirstNext.setVisibility(View.INVISIBLE);
            } else {
                binding.btnFirstBefore.setVisibility(View.VISIBLE);
                binding.textBtnFirstBefore.setVisibility(View.VISIBLE);
                binding.textBtnFirstBefore.setText(String.valueOf(contData));
            }
        }

        if (contData > 0){
            binding.btnFirstBefore.setVisibility(View.VISIBLE);
            binding.textBtnFirstBefore.setVisibility(View.VISIBLE);
        }

    }

    public void Click_Before_Data (View v){
        contData --;
        binding.textviewFirst.setText(arrayData.get(contData).getValue());
        binding.textBtnFirstNext.setText(String.valueOf(cantResult - contData));
        binding.textBtnFirstBefore.setText(String.valueOf(contData));

        if (contData == 0){
            binding.btnFirstBefore.setVisibility(View.INVISIBLE);
        } else {
            binding.btnFirstNext.setVisibility(View.VISIBLE);
            binding.textBtnFirstNext.setVisibility(View.VISIBLE);
            binding.textBtnFirstNext.setText(String.valueOf(cantResult - contData));
        }
    }
}