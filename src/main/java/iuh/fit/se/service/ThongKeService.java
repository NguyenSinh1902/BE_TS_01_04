package iuh.fit.se.service;

import iuh.fit.se.dto.thongke.BieuDoResponse;
import iuh.fit.se.dto.thongke.DashboardResponse;

import java.util.List;

public interface ThongKeService {
    DashboardResponse getDashboardHomNay();

    List<BieuDoResponse> getBieuDoTheoNgay();
}
