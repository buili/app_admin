package com.example.bt.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bt.R;
import com.example.bt.adapter.GioHangAdapter;
import com.example.bt.model.EventBus.TinhTongEvent;
import com.example.bt.model.GioHang;
import com.example.bt.model.SanPham;
import com.example.bt.retrofit.ApiBanHang;
import com.example.bt.retrofit.RetrofitClient;
import com.example.bt.ultil.Chung;
import com.example.bt.ultil.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GioHangActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView giohangtrong, tongtien;
    CheckBox checkBox;
    RecyclerView recyclerView;
    AppCompatButton btnmuahang;
    GioHangAdapter adapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    LinearLayoutManager linearLayoutManager;

    long tongTienSpMua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        InitView();
        ActionToolBar();
        InitControl();

        tinhTongTien();
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (adapter != null) {
                adapter.selectAll(isChecked); // Call the selectAll method on the adapter instance
            }
        });

    }



    private void tinhTongTien() {
        tongTienSpMua = 0;
        for(int i = 0; i < Utils.mangmuahang.size(); i++){
            tongTienSpMua += Utils.mangmuahang.get(i).getSanPham().getGia() * Utils.mangmuahang.get(i).getSoluong();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtien.setText(decimalFormat.format(tongTienSpMua) + " Đ");
    }

    private void InitControl() {
        if(Utils.manggiohang.size() == 0){
            giohangtrong.setVisibility(View.VISIBLE);
            //btnmuahang.setVisibility(View.INVISIBLE);
        }else{
            adapter = new GioHangAdapter(getApplicationContext(), Utils.manggiohang);
            recyclerView.setAdapter(adapter);
        }


        btnmuahang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.mangmuahang.size() == 0){
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn ít nhất một sản phẩm", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getApplicationContext(), ThanhToanActivity.class);
                    intent.putExtra("tongtien", tongTienSpMua);
                    Utils.manggiohang.clear();
                    startActivity(intent);
                }
            }
        });



//        int iduser = Utils.user_current.getId();
//        Log.e("GioHang", "idusser"+ iduser);
//        compositeDisposable.add(apiBanHang.giohang(iduser)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        gioHangModel -> {
//                            if(gioHangModel.isSuccess()){
//                                adapter = new GioHangAdapter(getApplicationContext(), gioHangModel.getResult());
//                                recyclerView.setAdapter(adapter);
//                            }
//                        },
//                        throwable -> {
//
//                            Log.e("GioHang", "Loi", throwable);
//                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                )
//        );


//        compositeDisposable.add(apiBanHang.giohang(1)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        gioHangModel -> {
//                            if (gioHangModel.isSuccess()) {
//                                List<GioHang> gioHangList = gioHangModel.getResult();
//                                if (gioHangList == null || gioHangList.isEmpty()) {
//                                    Toast.makeText(getApplicationContext(), "Giỏ hàng rỗng", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    for (GioHang gioHang : gioHangList) {
//                                        // Tạo đối tượng SanPham từ dữ liệu JSON
//                                        SanPham sanPham = gioHang.getSanPham();
//                                        if (sanPham == null) {
//                                            // Xử lý trường hợp sanPham là null
//                                            Log.e("GioHang", "SanPham is null in GioHang with id: " + gioHang.getIdsp());
//                                            continue; // Bỏ qua mục giỏ hàng này nếu sanPham là null
//                                        }
//                                        SanPham sanPhamData = new SanPham();
//                                        sanPhamData.setTen(sanPham.getTen());
//                                        sanPhamData.setHinhanh(sanPham.getHinhanh());
//                                        sanPhamData.setGia(sanPham.getGia());
//                                        sanPhamData.setMota(sanPham.getMota());
//                                        sanPhamData.setIdloai(sanPham.getIdloai());
//
//                                        // Gán đối tượng SanPham vào GioHang
//                                        gioHang.setSanPham(sanPhamData);
//                                    }
//                                    adapter = new GioHangAdapter(getApplicationContext(), gioHangList);
//                                    recyclerView.setAdapter(adapter);
//                                }
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Không lấy được dữ liệu giỏ hàng", Toast.LENGTH_SHORT).show();
//                            }
//                        },
//                        throwable -> {
//                            Log.e("GioHang", "Error fetching data", throwable);
//                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                )
//        );
    }

//    private void ActionToolBar() {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }

    private void ActionToolBar() {
        Chung.ActionToolBar(this, toolbar);
    }

    private void InitView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toolbargiohang);
        recyclerView = findViewById(R.id.recyclerviewgiohang);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        giohangtrong = findViewById(R.id.txtgiohangtrong);
        tongtien = findViewById(R.id.txttongtiengiohang);

        checkBox = findViewById(R.id.checkboxtong);

        btnmuahang = findViewById(R.id.btnmuahang);
    }

    @Override
    protected void onStart() {
        super.onStart();

            EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public  void eventTinhTien(TinhTongEvent event){
        if(event != null){
            tinhTongTien();
        }
    }
}