package com.example.bt.model;

import java.util.List;

public class LoaiSanPhamModel {
    boolean success;
    String message;
    List<LoaiSanPham> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<LoaiSanPham> getResult() {
        return result;
    }

    public void setResult(List<LoaiSanPham> result) {
        this.result = result;
    }
}
