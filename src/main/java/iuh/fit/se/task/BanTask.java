package iuh.fit.se.task;

import iuh.fit.se.service.PhieuDatBanService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BanTask {
    private final PhieuDatBanService phieuDatBanService;

    public BanTask(PhieuDatBanService phieuDatBanService) {
        this.phieuDatBanService = phieuDatBanService;
    }


    // Cron "0 * * * * *" nghĩa là: Giây thứ 0 của mỗi phút (Chạy mỗi phút 1 lần)
    @Scheduled(cron = "0 * * * * *")
    public void quetBanQuaHan() {
        phieuDatBanService.xuLyHuyPhieuQuaHan();
    }
}