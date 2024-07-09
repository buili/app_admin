package com.example.bt.model.EventBus;

import com.example.bt.model.SanPham;

public class SuaXoaEvent {
    SanPham sanPham;

    public SuaXoaEvent(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }
}
