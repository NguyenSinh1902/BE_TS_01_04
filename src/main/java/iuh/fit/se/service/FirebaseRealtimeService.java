package iuh.fit.se.service;

import iuh.fit.se.entity.Ban;

import java.util.List;

public interface FirebaseRealtimeService {
    void updateBanStatus(Integer idBan, String tenBan, String tinhTrang);
    void updateMultipleBansStatus(List<Ban> bans);
}
