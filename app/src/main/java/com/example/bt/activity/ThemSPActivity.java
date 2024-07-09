package com.example.bt.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bt.R;
import com.example.bt.model.LoaiSanPham;
import com.example.bt.model.MessageModel;
import com.example.bt.model.SanPham;
import com.example.bt.retrofit.ApiBanHang;
import com.example.bt.retrofit.RetrofitClient;
import com.example.bt.ultil.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemSPActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText edtTen, edtGia, edtHinhAnh, edtMoTa, edtSL;
    ImageView img;
    SanPham sanPhamSua;
    Spinner spinnerLoai;
    int loai = 0;
    AppCompatButton btnThem;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    LoaiSanPham loaiSanPham;
    List<LoaiSanPham> mangloaisp;
    String mediaPath;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_spactivity);
        initView();
        getLoai();
        actionToolBar();
        intiControl();

        Intent intent = getIntent();
        sanPhamSua = (SanPham) intent.getSerializableExtra("sua");
        if(sanPhamSua == null){
            flag = false;
        }else{
            flag = true;
            btnThem.setText("Sửa sản phẩm");
            edtTen.setText(sanPhamSua.getTen());
            edtGia.setText(sanPhamSua.getGia() + "");
            edtHinhAnh.setText(sanPhamSua.getHinhanh());
            edtMoTa.setText(sanPhamSua.getMota());
            //spinnerLoai.setSelection(sanPhamSua.getIdloai());
            int spinnerPosition = getSpinnerPositionById(sanPhamSua.getIdloai());
            spinnerLoai.setSelection(spinnerPosition);
            edtSL.setText(sanPhamSua.getSltonkho() + "");
        }

    }

    private void intiControl() {
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == false) {
                    themSP();
                }else {
                    suaSP();
                }
            }


        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ThemSPActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });
    }


    private void getLoai() {
        List<String> stringList = new ArrayList<>();
        stringList.add("Vui lòng chọn loại sản phẩm");
        compositeDisposable.add(apiBanHang.getloaisp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSanPhamModel -> {
                            if(loaiSanPhamModel.isSuccess()){
                                mangloaisp = loaiSanPhamModel.getResult();
                                for(int i = 0; i < mangloaisp.size(); i++){
                                    stringList.add(mangloaisp.get(i).getTenloaisanpham());
                                }
                                // Sau khi dữ liệu đã được nạp, thiết lập adapter và lựa chọn spinner
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(ThemSPActivity.this, android.R.layout.simple_spinner_dropdown_item, stringList);
                                spinnerLoai.setAdapter(adapter);

                                // Nếu đang sửa sản phẩm, thiết lập lựa chọn spinner
                                if(flag) {
                                    int spinnerPosition = getSpinnerPositionById(sanPhamSua.getIdloai());
                                    spinnerLoai.setSelection(spinnerPosition);
                                }
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Lấy loại sản phẩm bị lỗi", Toast.LENGTH_SHORT).show();
                        }
                )
        );
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stringList);
//        spinnerLoai.setAdapter(adapter);
        spinnerLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loai = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int getSpinnerPositionById(int idLoai) {
        for (int i = 0; i < mangloaisp.size(); i++) {
            if (mangloaisp.get(i).getId() == idLoai) {
                return i + 1; // Cộng thêm 1 vì phần tử đầu tiên là "Vui lòng chọn loại sản phẩm"
            }
        }
        return 0; // Mặc định chọn "Vui lòng chọn loại sản phẩm"
    }

    private void themSP() {
        String str_ten = edtTen.getText().toString().trim();
        String str_gia = edtGia.getText().toString().trim();
        String str_hinhanh = edtHinhAnh.getText().toString().trim();
        String str_mota = edtMoTa.getText().toString().trim();
        String str_soluong = edtSL.getText().toString().trim();
        if(TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_hinhanh)
        || TextUtils.isEmpty(str_mota) || TextUtils.isEmpty(str_soluong) || loai == 0){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ các trường", Toast.LENGTH_SHORT).show();
        }else{
            compositeDisposable.add(apiBanHang.themsp(str_ten, str_gia, str_hinhanh, str_mota, loai, Integer.parseInt(str_soluong))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                    if (messageModel.isSuccess()){
                                        Toast.makeText(getApplicationContext(), "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();

                                    }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(), "Thêm sản phẩm bị lỗi", Toast.LENGTH_SHORT).show();
                            }
                    )
            );
        }
    }

    private void suaSP() {

        String str_ten = edtTen.getText().toString().trim();
        String str_gia = edtGia.getText().toString().trim();
        String str_hinhanh = edtHinhAnh.getText().toString().trim();
        String str_mota = edtMoTa.getText().toString().trim();
        String str_soluong = edtSL.getText().toString().trim();
        if(TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_hinhanh)
                || TextUtils.isEmpty(str_mota) || TextUtils.isEmpty(str_soluong) || loai == 0){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ các trường", Toast.LENGTH_SHORT).show();
        }else{
            compositeDisposable.add(apiBanHang.suasp(sanPhamSua.getId(),str_ten, str_gia, str_hinhanh, str_mota, loai, Integer.parseInt(str_soluong))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if (messageModel.isSuccess()){
                                    Toast.makeText(getApplicationContext(), "Sửa sản phẩm thành công", Toast.LENGTH_SHORT).show();

                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(), "Thêm sản phẩm bị lỗi", Toast.LENGTH_SHORT).show();
                            }
                    )
            );
        }
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

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toolbarthemsp);
        edtTen = findViewById(R.id.edttensp);
        edtGia = findViewById(R.id.edtgiasp);
        edtHinhAnh = findViewById(R.id.edthinhanhsp);
        edtMoTa = findViewById(R.id.edtmotasp);
        edtSL = findViewById(R.id.edtsoluongsp);
        spinnerLoai = findViewById(R.id.spinnerloai);
        btnThem = findViewById(R.id.btnthemsp);
        img = findViewById(R.id.imgthemsp);
        mangloaisp  = new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPath = data.getDataString();
        uploadMultipleFiles();
        Log.d("log", "onActivityResult" + mediaPath);
    }

    private String getPath(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    private void uploadMultipleFiles() {
        Uri uri = Uri.parse(mediaPath);
        File file = new File(getPath(uri));
        // Parsing any Media type file
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);
        Call<MessageModel> call = apiBanHang.uploadFile(fileToUpload1);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                MessageModel serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.isSuccess()) {
                        edtHinhAnh.setText(serverResponse.getName());
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Log.v("Response", serverResponse.toString());
                }

            }

            @Override
            public void onFailure(Call<MessageModel> call, Throwable t) {
                Log.d("log", t.getMessage());
            }
        });
    }
}