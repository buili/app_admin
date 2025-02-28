package com.example.bt.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bt.R;
import com.example.bt.adapter.SanPhamAdapter;
import com.example.bt.model.SanPham;
import com.example.bt.retrofit.ApiBanHang;
import com.example.bt.retrofit.RetrofitClient;
import com.example.bt.ultil.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TimKiemActivity extends AppCompatActivity {
    EditText edtTimKiem;
    Toolbar toolbar;
    RecyclerView recyclerView;
    SanPhamAdapter sanPhamAdapter;

    List<SanPham> mangSanPham;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_kiem);
        initView();
        actionToolBar();
    }

    private void actionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void timkiem(String s){
        mangSanPham.clear();
        compositeDisposable.add(apiBanHang.timkiem(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamModel -> {
                            if(sanPhamModel.isSuccess()){
                                mangSanPham = sanPhamModel.getResult();
                                sanPhamAdapter = new SanPhamAdapter(getApplicationContext(), mangSanPham);
                                recyclerView.setAdapter(sanPhamAdapter);
                            }else{
                                mangSanPham.clear();
                                sanPhamAdapter = new SanPhamAdapter(getApplicationContext(), mangSanPham);
                                recyclerView.setAdapter(sanPhamAdapter);
                            }

                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "tìm kiếm lỗi", Toast.LENGTH_SHORT).show();
                            mangSanPham.clear();
                        }
                )
        );
    }

    private void initView() {

        mangSanPham = new ArrayList<>();
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toolbartimkiem);
        edtTimKiem = findViewById(R.id.edttimkiem);
        recyclerView = findViewById(R.id.recyclerviewtimkiem);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length() == 0){
                        mangSanPham.clear();
                        sanPhamAdapter = new SanPhamAdapter(getApplicationContext(), mangSanPham);
                        recyclerView.setAdapter(sanPhamAdapter);
                    }else{
                        timkiem(s.toString());
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}