package com.example.bt.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.bt.R;
import com.example.bt.adapter.DonHangAdapter;
import com.example.bt.model.DonHang;
import com.example.bt.model.EventBus.DonHangEvent;
import com.example.bt.model.Item;
import com.example.bt.retrofit.ApiBanHang;
import com.example.bt.retrofit.RetrofitClient;
import com.example.bt.ultil.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DonHangActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView recyclerView;
    Toolbar toolbar;
    DonHang donHang;
    int tinhtrang;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang);
        inintView();
        actionToolbar();
        donHang();
    }

    private void donHang() {
        compositeDisposable.add(apiBanHang.donhang(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            DonHangAdapter donHangAdapter = new DonHangAdapter(getApplicationContext(), donHangModel.getResult());
                            recyclerView.setAdapter(donHangAdapter);
                            donHangAdapter.notifyDataSetChanged();
                        },
                        throwable -> {
                            Log.e("DonHangActivity", "Lỗi: " + throwable);
                            Toast.makeText(getApplicationContext(), "Lỗi " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                )
        );
    }



    private void actionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void inintView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        recyclerView = findViewById(R.id.recyclerview_donhang);
        toolbar = findViewById(R.id.toolbardonhang);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void showCustomDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_donhang, null);
        Spinner spinner = view.findViewById(R.id.spinner_dialog);
        AppCompatButton btndongy = view.findViewById(R.id.dongy_dialog);

        List<String> list = new ArrayList<>();
        list.add("Chờ xác nhận");
        list.add("Chờ lấy hàng");
        list.add("Chờ giao hàng");
        list.add("Đã giao");
        list.add("Đã hủy");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        spinner.setSelection(donHang.getTrangthai());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tinhtrang = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btndongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capNhapDonHang();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void capNhapDonHang() {
        compositeDisposable.add(apiBanHang.updateorder(donHang.getId(), tinhtrang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            donHang();
                            dialog.dismiss();
                        },
                        throwable -> {

                        }
                ));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void evenDonHang(DonHangEvent event) {
        if (event != null) {
            donHang = event.getDonHang();
            showCustomDialog();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}