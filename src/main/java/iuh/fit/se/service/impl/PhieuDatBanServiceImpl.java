package iuh.fit.se.service.impl;

import iuh.fit.se.dto.ban.BanResponse;
import iuh.fit.se.dto.phieudatban.PhieuDatBanRequest;
import iuh.fit.se.dto.phieudatban.PhieuDatBanResponse;
import iuh.fit.se.entity.Ban;
import iuh.fit.se.entity.ChiTietDatBan;
import iuh.fit.se.entity.PhieuDatBan;
import iuh.fit.se.enums.TinhTrangBan;
import iuh.fit.se.enums.TrangThaiDatBan;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.exception.NotFoundException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.mapper.BanMapper;
import iuh.fit.se.mapper.PhieuDatBanMapper;
import iuh.fit.se.repository.BanRepository;
import iuh.fit.se.repository.ChiTietDatBanRepository;
import iuh.fit.se.repository.HoaDonRepository;
import iuh.fit.se.repository.PhieuDatBanRepository;
import iuh.fit.se.service.FirebaseMessagingService;
import iuh.fit.se.service.FirebaseRealtimeService;
import iuh.fit.se.service.PhieuDatBanService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhieuDatBanServiceImpl implements PhieuDatBanService {

    private final PhieuDatBanRepository phieuDatBanRepository;
    private final ChiTietDatBanRepository chiTietDatBanRepository;
    private final BanRepository banRepository;
    private final PhieuDatBanMapper phieuDatBanMapper;
    private final BanMapper banMapper;
    private final HoaDonRepository hoaDonRepository;
    private final FirebaseRealtimeService firebaseRealtimeService;
    private final FirebaseMessagingService firebaseMessagingService;

    public PhieuDatBanServiceImpl(PhieuDatBanRepository phieuDatBanRepository, ChiTietDatBanRepository chiTietDatBanRepository, BanRepository banRepository, PhieuDatBanMapper phieuDatBanMapper, BanMapper banMapper, HoaDonRepository hoaDonRepository, FirebaseRealtimeService firebaseRealtimeService, FirebaseMessagingService firebaseMessagingService) {
        this.phieuDatBanRepository = phieuDatBanRepository;
        this.chiTietDatBanRepository = chiTietDatBanRepository;
        this.banRepository = banRepository;
        this.phieuDatBanMapper = phieuDatBanMapper;
        this.banMapper = banMapper;
        this.hoaDonRepository = hoaDonRepository;
        this.firebaseRealtimeService = firebaseRealtimeService;
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @Override
    @Transactional
    public PhieuDatBanResponse taoPhieuDatMoi(PhieuDatBanRequest request) {
        PhieuDatBan phieu = phieuDatBanMapper.toEntity(request);
        if (request.thoiGianDat().isBefore(LocalDateTime.now().plusMinutes(5))) {
            phieu.setTrangThaiDat(TrangThaiDatBan.DA_DEN);
        } else {
            phieu.setTrangThaiDat(TrangThaiDatBan.CHO_DEN);
        }

        PhieuDatBan savedPhieu = phieuDatBanRepository.save(phieu);
        List<BanResponse> banResponses = new ArrayList<>();
        List<Ban> listBanThayDoi = new ArrayList<>(); // Danh sách để đẩy realtime

        for (Integer idBan : request.danhSachIdBan()) {
            Ban ban = banRepository.findActiveById(idBan)
                    .orElseThrow(() -> new ResourceNotFoundException("Bàn " + idBan + " không tồn tại"));

            if (ban.getTinhTrangBan() != TinhTrangBan.TRONG) {
                throw new BadRequestException("Bàn " + ban.getTenBan() + " hiện không trống!");
            }

            ChiTietDatBan chiTiet = new ChiTietDatBan();
            chiTiet.setPhieuDatBan(savedPhieu);
            chiTiet.setBan(ban);
            chiTietDatBanRepository.save(chiTiet);

            ban.setTinhTrangBan(savedPhieu.getTrangThaiDat() == TrangThaiDatBan.DA_DEN ?
                    TinhTrangBan.CO_KHACH : TinhTrangBan.DA_DAT);

            banRepository.save(ban);
            listBanThayDoi.add(ban); // Lưu lại bàn đã đổi trạng thái
            banResponses.add(banMapper.toResponse(ban));
        }

        // REALTIME: Cập nhật sơ đồ bàn cho tất cả nhân viên
        firebaseRealtimeService.updateMultipleBansStatus(listBanThayDoi);

        // THÔNG BÁO ĐẨY: Chỉ gửi cho nhóm nhân viên (Phục vụ & Thu ngân)
        String title = "Đơn đặt bàn mới!";
        String body = "Khách " + savedPhieu.getTenKhachHang() + " vừa đặt " + request.danhSachIdBan().size() + " bàn.";

        // Gửi vào topic 'staff' - FE của Thu ngân và Phục vụ sẽ subscribe topic này
        firebaseMessagingService.sendNotificationToTopic("staff", title, body);

        return setBanToResponse(phieuDatBanMapper.toResponse(savedPhieu), banResponses);
    }

    @Override
    @Transactional
    public void doiBan(Integer idPhieu, Integer idBanCu, Integer idBanMoi) {
        PhieuDatBan phieu = phieuDatBanRepository.findActiveById(idPhieu)
                .orElseThrow(() -> new NotFoundException("Phiếu đặt bàn không tồn tại!"));

        ChiTietDatBan ctCu = chiTietDatBanRepository.findByPhieuDatBan_IdPhieuDat(idPhieu)
                .stream()
                .filter(ct -> ct.getBan().getIdBan().equals(idBanCu))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Bàn cũ không thuộc phiếu này!"));

        Ban banCu = ctCu.getBan();
        banCu.setTinhTrangBan(TinhTrangBan.TRONG);
        banRepository.save(banCu);
        chiTietDatBanRepository.delete(ctCu);

        Ban banMoi = banRepository.findActiveById(idBanMoi)
                .orElseThrow(() -> new NotFoundException("Bàn mới không tồn tại!"));
        if (banMoi.getTinhTrangBan() != TinhTrangBan.TRONG) throw new BadRequestException("Bàn mới đang bận!");

        ChiTietDatBan ctMoi = new ChiTietDatBan();
        ctMoi.setPhieuDatBan(ctCu.getPhieuDatBan());
        ctMoi.setBan(banMoi);
        chiTietDatBanRepository.save(ctMoi);

        banMoi.setTinhTrangBan(TinhTrangBan.CO_KHACH);
        banRepository.save(banMoi);

        // REALTIME: Cập nhật đồng thời cả 2 bàn
        firebaseRealtimeService.updateMultipleBansStatus(List.of(banCu, banMoi));
    }

    @Override
    @Transactional
    public void gopThemBan(Integer idPhieu, List<Integer> idBansMoi) {
        PhieuDatBan phieu = phieuDatBanRepository.findActiveById(idPhieu)
                .orElseThrow(() -> new NotFoundException("Phiếu đặt bàn không tồn tại!"));
        for (Integer id : idBansMoi) {
            Ban ban = banRepository.findActiveById(id)
                    .orElseThrow(() -> new NotFoundException("Bàn " + id + " không tồn tại!"));
            if (ban.getTinhTrangBan() != TinhTrangBan.TRONG) throw new BadRequestException("Bàn " + ban.getTenBan() + " không trống!");

            ChiTietDatBan ct = new ChiTietDatBan();
            ct.setPhieuDatBan(phieu);
            ct.setBan(ban);
            chiTietDatBanRepository.save(ct);

            ban.setTinhTrangBan(TinhTrangBan.CO_KHACH);
            banRepository.save(ban);
        }
    }

    @Override
    @Transactional
    public void huyPhieu(Integer idPhieu) {
        // 1. Tìm phiếu và kiểm tra điều kiện (Code cũ của bạn)
        PhieuDatBan phieu = phieuDatBanRepository.findActiveById(idPhieu)
                .orElseThrow(() -> new ResourceNotFoundException("Phiếu không tồn tại hoặc đã được xử lý"));

        boolean daCoHoaDon = hoaDonRepository.existsByPhieuDatBan_IdPhieuDatAndThoiGianXoa(idPhieu, 0L);

        if (daCoHoaDon) {
            throw new BadRequestException("Không thể hủy phiếu vì đã có hóa đơn được tạo. Hãy xử lý hóa đơn trước!");
        }

        // 2. Cập nhật trạng thái phiếu
        phieu.setTrangThaiDat(TrangThaiDatBan.DA_HUY);

        // 3. Giải phóng bàn và chuẩn bị danh sách cho Realtime
        List<ChiTietDatBan> chiTiets = chiTietDatBanRepository.findByPhieuDatBan_IdPhieuDat(idPhieu);
        List<Ban> bansToUpdate = new ArrayList<>(); // List này để gửi sang Firebase

        for (ChiTietDatBan ct : chiTiets) {
            Ban ban = ct.getBan();
            ban.setTinhTrangBan(TinhTrangBan.TRONG); // Chuyển về TRỐNG
            bansToUpdate.add(ban);
        }

        // 4. Lưu tất cả thay đổi vào SQL (Quan trọng: dùng saveAll cho List bàn)
        banRepository.saveAll(bansToUpdate);
        phieuDatBanRepository.save(phieu);

        // 5. REALTIME: Đẩy tín hiệu lên Firebase để các App đổi màu bàn sang XANH
        firebaseRealtimeService.updateMultipleBansStatus(bansToUpdate);

        // THÔNG BÁO ĐẨY: Báo tin buồn để nhân viên biết bàn đã trống
        String title = "Hủy phiếu đặt bàn";
        String body = "Phiếu của khách " + phieu.getTenKhachHang() + " đã bị hủy. Bàn hiện đã trống.";

        firebaseMessagingService.sendNotificationToTopic("staff", title, body);
    }

    @Override
    @Transactional
    public void giaiPhongBanKhiHuyHoaDon(Integer idPhieu) {
        PhieuDatBan phieu = phieuDatBanRepository.findById(idPhieu)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phiếu đặt bàn với id = " + idPhieu));

        if (phieu.getTrangThaiDat() == TrangThaiDatBan.DA_HUY) {
            return;
        }

        phieu.setTrangThaiDat(TrangThaiDatBan.DA_HUY);

        List<ChiTietDatBan> chiTiets = chiTietDatBanRepository.findByPhieuDatBan_IdPhieuDat(idPhieu);
        for (ChiTietDatBan ct : chiTiets) {
            Ban ban = ct.getBan();
            ban.setTinhTrangBan(TinhTrangBan.TRONG);
        }
        phieuDatBanRepository.save(phieu);
    }

    @Override
    @Transactional
    public void xuLyHuyPhieuQuaHan() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(1);
        List<PhieuDatBan> quaHan = phieuDatBanRepository.findByTrangThaiDatAndThoiGianDatBefore(TrangThaiDatBan.CHO_DEN, threshold);
        quaHan.forEach(p -> huyPhieu(p.getIdPhieuDat()));
    }

    private PhieuDatBanResponse setBanToResponse(PhieuDatBanResponse res, List<BanResponse> bans) {
        return new PhieuDatBanResponse(
                res.idPhieuDat(), res.tenKhachHang(), res.sdtKhachHang(),
                res.thoiGianDat(), res.soLuongNguoi(), res.trangThaiDat(),
                res.ghiChu(), bans
        );
    }

    @Override
    @Transactional
    public void hoanTatPhieu(Integer idPhieu) {
        // 1. Tìm phiếu
        PhieuDatBan phieu = phieuDatBanRepository.findActiveById(idPhieu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu đặt để hoàn tất"));

        // 2. Cập nhật trạng thái phiếu
        phieu.setTrangThaiDat(TrangThaiDatBan.HOAN_TAT);
        phieuDatBanRepository.save(phieu);

        // 3. Giải phóng bàn và chuẩn bị danh sách Realtime
        List<ChiTietDatBan> chiTiets = chiTietDatBanRepository.findByPhieuDatBan_IdPhieuDat(idPhieu);
        List<Ban> bansToUpdate = new ArrayList<>();

        for (ChiTietDatBan ct : chiTiets) {
            Ban ban = ct.getBan();
            ban.setTinhTrangBan(TinhTrangBan.TRONG);
            bansToUpdate.add(ban);
        }

        // 4. Lưu vào SQL
        banRepository.saveAll(bansToUpdate);

        // 5. REALTIME: Cập nhật sơ đồ bàn trên toàn hệ thống
        firebaseRealtimeService.updateMultipleBansStatus(bansToUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhieuDatBanResponse> layTatCaPhieu() {
        return phieuDatBanRepository.findAll().stream()
                .map(this::convertToResponseWithBans)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhieuDatBanResponse> layPhieuDangHoatDong() {
        return phieuDatBanRepository.findAll().stream()
                .filter(p -> p.getTrangThaiDat() == TrangThaiDatBan.DA_DEN || p.getTrangThaiDat() == TrangThaiDatBan.CHO_DEN)
                .map(this::convertToResponseWithBans)
                .collect(Collectors.toList());
    }

    private PhieuDatBanResponse convertToResponseWithBans(PhieuDatBan phieu) {
        List<ChiTietDatBan> chiTiets = chiTietDatBanRepository.findByPhieuDatBan_IdPhieuDat(phieu.getIdPhieuDat());

        List<BanResponse> banResponses = chiTiets.stream()
                .map(ct -> banMapper.toResponse(ct.getBan()))
                .collect(Collectors.toList());

        PhieuDatBanResponse res = phieuDatBanMapper.toResponse(phieu);

        return new PhieuDatBanResponse(
                res.idPhieuDat(),
                res.tenKhachHang(),
                res.sdtKhachHang(),
                res.thoiGianDat(),
                res.soLuongNguoi(),
                res.trangThaiDat(),
                res.ghiChu(),
                banResponses
        );
    }

    @Override
    @Transactional
    public PhieuDatBanResponse checkIn(Integer idPhieu) {
        PhieuDatBan phieu = phieuDatBanRepository.findById(idPhieu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu đặt ID: " + idPhieu));

        if (phieu.getTrangThaiDat() != TrangThaiDatBan.CHO_DEN) {
            throw new BadRequestException("Phiếu này không ở trạng thái chờ đến, không thể check-in!");
        }

        phieu.setTrangThaiDat(TrangThaiDatBan.DA_DEN);
        phieuDatBanRepository.save(phieu);

        List<ChiTietDatBan> chiTiets = chiTietDatBanRepository.findByPhieuDatBan_IdPhieuDat(idPhieu);
        List<BanResponse> banResponses = new ArrayList<>();
        List<Ban> bansToUpdate = new ArrayList<>();

        for (ChiTietDatBan ct : chiTiets) {
            Ban ban = ct.getBan();
            ban.setTinhTrangBan(TinhTrangBan.CO_KHACH);
            banRepository.save(ban);
            bansToUpdate.add(ban);
            banResponses.add(banMapper.toResponse(ban));
        }

        // REALTIME: Đổi màu bàn từ vàng (Đã đặt) sang đỏ (Có khách)
        firebaseRealtimeService.updateMultipleBansStatus(bansToUpdate);

        return setBanToResponse(phieuDatBanMapper.toResponse(phieu), banResponses);
    }
}