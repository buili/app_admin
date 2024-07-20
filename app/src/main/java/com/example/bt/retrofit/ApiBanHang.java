package com.example.bt.retrofit;


import com.example.bt.model.DonHangModel;
import com.example.bt.model.GioHangModel;
import com.example.bt.model.LoaiSanPhamModel;
import com.example.bt.model.MenuModel;
import com.example.bt.model.MessageModel;
import com.example.bt.model.SanPhamModel;
import com.example.bt.model.ThongKeModel;
import com.example.bt.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiBanHang {
    @GET("getmenu.php")
    Observable<MenuModel> getmenu();

    @GET("getspmoinhat.php")
    Observable<SanPhamModel> getspmoinhat();

    @POST("getsp.php")
    @FormUrlEncoded
    Observable<SanPhamModel> getsp(
            @Field("page") int page,
            @Field("idloai") int idloai
    );

    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangnhap(
            @Field("email") String email,
            @Field("pass") String pass
    );

    @POST("giohang.php")
    @FormUrlEncoded
    Observable<GioHangModel> giohang(
            @Field("iduser") int iduser
    );

    @POST("themgiohang.php")
    @FormUrlEncoded
    Observable<GioHangModel> themgiohang(
            @Field("iduser") int iduser,
            @Field("idsp") int idsp,
            @Field("soluong") int soluong
    );

    @POST("updategiohang.php")
    @FormUrlEncoded
    Observable<GioHangModel> updategiohang(
            @Field("iduser") int iduser,
            @Field("idsp") int idsp,
            @Field("soluong") int soluong
    );

    @POST("xoaspgiohang.php")
    @FormUrlEncoded
    Observable<GioHangModel> xoaspgiohang(
            @Field("iduser") int iduser,
            @Field("idsp") int idsp


    );

    @POST("themdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> themdonhang(
      @Field("iduser") int iduser,
      @Field("sdt") String sdt,
      @Field("email") String email,
      @Field("diachi") String diachi,
      @Field("soluong") int soluong,
      @Field("tongtien") long tongtien,
      @Field("chitiet") String chitiet
    );

    @POST("xoagiohang.php")
    @FormUrlEncoded
    Observable<GioHangModel> xoagiohang(
      @Field("iduser") int iduser,
      @Field("idsp") int idsp
    );

    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamModel> timkiem(
       @Field("timkiem") String timkiem
    );

    @POST("dangky.php")
    @FormUrlEncoded
    Observable<UserModel> dangky(
      @Field("email") String email,
      @Field("username") String username,
      @Field("mobile") String mobile,
      @Field("pass") String pass,
      @Field("uid") String uid
    );

    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken(
            @Field("id") int id,
            @Field("token") String token
    );

    @POST("xoasp.php")
    @FormUrlEncoded
    Observable<MessageModel> xoasp(
            @Field("id") int id
    );

    @POST("themsp.php")
    @FormUrlEncoded
    Observable<MessageModel> themsp(
            @Field("ten") String ten,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("idloai") int idloai,
            @Field("sltonkho") int sltonkho
    );

    @POST("suasp.php")
    @FormUrlEncoded
    Observable<MessageModel> suasp(
            @Field("id") int id,
            @Field("ten") String ten,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("idloai") int idloai,
            @Field("sltonkho") int sltonkho
    );

    @GET("getloaisp.php")
    Observable<LoaiSanPhamModel> getloaisp();

    @Multipart
    @POST("uploadhinhanh.php")
    Call<MessageModel> uploadFile(@Part MultipartBody.Part file);

    @GET("thongkespban.php")
    Observable<ThongKeModel> getthongkespban();

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> donhang(
            @Field("iduser") int id
    );

    @POST("updateorder.php")
    @FormUrlEncoded
    Observable<MessageModel> updateorder(
            @Field("id") int id,
            @Field("trangthai") int trangthai
    );
}
