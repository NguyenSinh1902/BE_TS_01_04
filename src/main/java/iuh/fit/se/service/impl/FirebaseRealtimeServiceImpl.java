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
            ref.setValueAsync(data);
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật Firebase Order: " + e.getMessage());
        }
    }
}
