package com.example.bt.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.bt.Abstract.BottomNavigationActivity;
import com.example.bt.R;
import com.example.bt.adapter.MenuAdapter;
import com.example.bt.adapter.SanPhamMoiAdapter;
import com.example.bt.model.Menu_1;
import com.example.bt.model.SanPham;
import com.example.bt.model.User;
import com.example.bt.retrofit.ApiBanHang;
import com.example.bt.retrofit.RetrofitClient;
import com.example.bt.ultil.CheckConnection;
import com.example.bt.ultil.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends BottomNavigationActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ViewFlipper viewFlipper;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ListView listViewMain;

    List<Menu_1> mangmenu;
    MenuAdapter menuAdapter;

    List<SanPham> mangSp;
    SanPhamMoiAdapter sanPhamAdapter;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        if(Paper.book().read("user") != null){
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }

       getToken();
        intiView();
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            ActionBar();
            ActionViewFlipper();
            GetMenu();
            GetSpMoiNhat();
            CatchOnItemListView();
        } else {
            CheckConnection.showToast_Short(getApplicationContext(), "Bạn hãy kiểm tra lại kết nối wifi");
            finish();
        }

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.home_bottom;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menugiohang);
        if (menuItem != null) {
            View actionView = menuItem.getActionView();
            if (actionView != null) {
                ImageView cartIcon = actionView.findViewById(R.id.cart_icon);
                TextView cartBadge = actionView.findViewById(R.id.cart_badge);
                updateCartBadge(cartBadge);

                // Set up the click listener for the cart icon
                actionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onOptionsItemSelected(menuItem);
                    }
                });
            } else {
                Log.e("MainActivity", "Action view for cart menu item is null");
            }
        } else {
            Log.e("MainActivity", "Menu item menugiohang not found");
        }
        return true;
    }

    private void updateCartBadge(TextView cartBadge) {
        int cartItemCount = getCartItemCount(); // Replace with actual cart item count
        if (cartItemCount > 0) {
            cartBadge.setText(String.valueOf(cartItemCount));
            cartBadge.setVisibility(View.VISIBLE);
        } else {
            //cartBadge.setVisibility(View.GONE);
        }
    }

    private int getCartItemCount() {
        // Replace with logic to get the actual number of items in the cart

        return Utils.manggiohang != null ? Utils.manggiohang.size() : 0;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menugiohang) {
            Intent intent = new Intent(getApplicationContext(), GioHangActivity.class);
            startActivity(intent);
            return true;
        }
        if(item.getItemId() == R.id.menutimkiem){
            Intent intent = new Intent(getApplicationContext(), TimKiemActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void CatchOnItemListView() {
        listViewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            CheckConnection.showToast_Short(getApplicationContext(), "Vui long kiem tra lai ket noi internet");
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 1:
                        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                            int idloai = mangmenu.get(position).getId();
                            Log.d("MainActivity", "idloai trước khi truyền: " + idloai);
                            Intent intent = new Intent(MainActivity.this, SanPhamActivity.class);
                            intent.putExtra("idloai", mangmenu.get(position).getId());
                            startActivity(intent);
                        } else {
                            CheckConnection.showToast_Short(getApplicationContext(), "Bạn hãy kiểm tra lại kết nối mạng");
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 2:
                        if(CheckConnection.haveNetworkConnection(getApplicationContext())){
                            int idloai = mangmenu.get(position).getId();
                            Log.d("MainActivity", "idloai trước khi truyền: " + idloai);
                            Intent intent = new Intent(MainActivity.this, SanPhamActivity.class);
                            intent.putExtra("idloai", mangmenu.get(position).getId());
                            startActivity(intent);
                        } else {
                            CheckConnection.showToast_Short(getApplicationContext(), "Bạn hãy kiểm tra lại kết nối mạng");
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 4:
                        Intent donhang = new Intent(getApplicationContext(), DonHangActivity.class);
                        startActivity(donhang);
                        break;
                    case 6:
                        Intent chat = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(chat);
                        break;
                    case 7:
                        Intent thongke = new Intent(getApplicationContext(), ThongKeActivity.class);
                        startActivity(thongke);
                        break;
                    case 8:
                        Paper.book().delete("user");
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(intent);
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 9:
                        Intent quanly = new Intent(getApplicationContext(), QuanLyActivity.class);
                        startActivity(quanly);

                        break;
                }
            }
        });
    }

    private void GetSpMoiNhat() {
        compositeDisposable.add(apiBanHang.getspmoinhat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamModel -> {
                            if (sanPhamModel.isSuccess()) {
                                mangSp = sanPhamModel.getResult();
                                sanPhamAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSp);
                                recyclerView.setAdapter(sanPhamAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "lỗi " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                )
        );
    }


    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void ActionViewFlipper() {
        ArrayList<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://media-cdn-v2.laodong.vn/storage/newsportal/2023/8/26/1233821/Giai-Nhi-1--Nang-Tre.jpg");
        mangquangcao.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS31qZj9VmsHL0-dTRbu_uAXHl5sD-vqVl7lg&s");
        mangquangcao.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQflpbFu6iB31LDfzn4SqY9DPSY3-td6SxUYQ&s");
        mangquangcao.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSvjx3jlhsXma8HSCTnQqrBuNItz-Kmba-8Cg&s");
        for (int i = 0; i < mangquangcao.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext())
                    .load(mangquangcao.get(i))
                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(5000);
        viewFlipper.setAutoStart(true);

        Animation animation_slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation animation_slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(animation_slide_in);
        viewFlipper.setOutAnimation(animation_slide_out);
    }

    private void GetMenu() {
        compositeDisposable.add(apiBanHang.getmenu()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        menuModel -> {
                            if (menuModel.isSuccess()) {
                                mangmenu = menuModel.getResult();
                                menuAdapter = new MenuAdapter(getApplicationContext(), mangmenu);
                                listViewMain.setAdapter(menuAdapter);
                            }
                        }, throwable -> {
                            Log.e("GetSpError", "Error while getting product types", throwable);
                            Toast.makeText(getApplicationContext(), "khong ket noi duoc" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                )
        );
    }

    private void intiView() {
        toolbar = findViewById(R.id.toolbarmain);
        recyclerView = findViewById(R.id.recycleviewmain);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        viewFlipper = findViewById(R.id.viewflipper);
        drawerLayout = findViewById(R.id.drawerlayoutmain);
        navigationView = findViewById(R.id.navigationview);
        listViewMain = findViewById(R.id.listviewmain);

        mangmenu = new ArrayList<>();

        mangSp = new ArrayList<>();

        if(Utils.manggiohang == null){
            Utils.manggiohang = new ArrayList<>();
        }else{
//            int totalItem = 0;
//            for(int i = 0; i < Utils.manggiohang.size(); i++){
//                totalItem = totalItem + Utils.manggiohang.get(i).getSoluongsp();
//            }
            getgiohang();
        }

    }

    private void getgiohang(){
        compositeDisposable.add(apiBanHang.giohang(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        gioHangModel -> {

                            //gioHangAdapter = new GioHangAdapter(getApplicationContext(), gioHangModel.getResult());
                            if (gioHangModel.getResult() == null || gioHangModel.getResult().isEmpty()) {
                                // Xử lý trường hợp giỏ hàng trống
                                Utils.manggiohang = new ArrayList<>(); // Khởi tạo danh sách trống
                            } else {
                                Utils.manggiohang = gioHangModel.getResult();
                            }
                            invalidateOptionsMenu();
                        },
                        throwable -> {
                            Log.e("DangNhap", "Lỗi main" + throwable);
                            Toast.makeText(getApplicationContext(), "lôi user" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                )
        );
    }

    private  void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(!TextUtils.isEmpty(s)){
                            compositeDisposable.add(apiBanHang.updateToken(Utils.user_current.getId(), s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            messageModel -> {

                                            },
                                            throwable -> {
                                                Log.d("log", throwable.getMessage());
                                            }
                                    )
                            );
                        }
                    }
                });
    }
    @Override
    protected void onResume() {
        super.onResume();
//        int totalItem = 0;
//        if (Utils.manggiohang != null) {
//            for (int i = 0; i < Utils.manggiohang.size(); i++) {
//                totalItem += Utils.manggiohang.get(i).getSoluongsp();
//            }
//        }
        getgiohang();
        invalidateOptionsMenu();

    }


}