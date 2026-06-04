package iuh.fit.se.service.impl;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import iuh.fit.se.entity.Ban;
import iuh.fit.se.service.FirebaseRealtimeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FirebaseRealtimeServiceImpl implements FirebaseRealtimeService {

    @Override
    public void updateBanStatus(Integer idBan, String tenBan, String tinhTrang) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tables/" + idBan);
        Map<String, Object> data = new HashMap<>();
        data.put("idBan", idBan);
        data.put("tenBan", tenBan);
        data.put("tinhTrang", tinhTrang);
        data.put("lastUpdate", System.currentTimeMillis());
        ref.setValueAsync(data);
    }

    @Override
    public void updateMultipleBansStatus(List<Ban> bans) {
        if (bans != null && !bans.isEmpty()) {
            bans.forEach(ban -> updateBanStatus(ban.getIdBan(), ban.getTenBan(), ban.getTinhTrangBan().name()));
        }
    }

    @Override
    public void updateOrderRealtime(iuh.fit.se.entity.HoaDon hd) {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders/" + hd.getIdHoaDon());
            Map<String, Object> data = new HashMap<>();
            data.put("idHoaDon", hd.getIdHoaDon());
            data.put("trangThai", hd.getTrangThai().name());
            data.put("tongThanhToan", hd.getTongThanhToan().doubleValue()); // Chuyển BigDecimal sang double
            data.put("lastUpdate", System.currentTimeMillis());

            // Serialize full object for Barista App
            if (hd.getLoaiDonHang() != null) {
                data.put("loaiDonHang", hd.getLoaiDonHang().name());
            }
            if (hd.getThoiGianTao() != null) {
                data.put("thoiGianTao", hd.getThoiGianTao().toString());
            }
            if (hd.getKhachHang() != null) {
                data.put("tenKhachHang", hd.getKhachHang().getHoTen());
            }
            data.put("ghiChu", hd.getGhiChuKhachHang());

            // Bỏ phần lấy tenBan vì PhieuDatBan không map trực tiếp với Ban
            // Nếu cần thiết, có thể dùng repository để query sau.

            List<Map<String, Object>> listMon = new java.util.ArrayList<>();
            if (hd.getDanhSachChiTiet() != null) {
                for (iuh.fit.se.entity.ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                    Map<String, Object> ctMap = new HashMap<>();
                    ctMap.put("idChiTiet", ct.getIdChiTiet()); // Quan trọng cho nút Xong món
                    ctMap.put("soLuong", ct.getSoLuong());
                    ctMap.put("tuyChonJson", ct.getTuyChonJson());

                    if (ct.getBienThe() != null) {
                        ctMap.put("tenKichCo", ct.getBienThe().getTenKichCo());
                        if (ct.getBienThe().getSanPham() != null) {
                            ctMap.put("tenSanPham", ct.getBienThe().getSanPham().getTenSanPham());
                        }
                    }

                    List<String> toppings = new java.util.ArrayList<>();
                    if (ct.getDanhSachTopping() != null) {
                        for (iuh.fit.se.entity.ChiTietHoaDonTopping tp : ct.getDanhSachTopping()) {
                            if (tp.getTopping() != null) {
                                toppings.add(tp.getTopping().getTenSanPham());
                            }
                        }
                    }
                    ctMap.put("danhSachTopping", toppings);
                    listMon.add(ctMap);
                }
            }
            data.put("danhSachChiTiet", listMon);

            ref.setValueAsync(data);
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật Firebase Order: " + e.getMessage());
        }
    }
}
