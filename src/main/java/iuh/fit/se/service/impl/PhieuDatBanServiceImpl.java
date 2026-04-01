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

    public PhieuDatBanServiceImpl(PhieuDatBanRepository phieuDatBanRepository, ChiTietDatBanRepository chiTietDatBanRepository, BanRepository banRepository, PhieuDatBanMapper phieuDatBanMapper, BanMapper banMapper, HoaDonRepository hoaDonRepository) {
        this.phieuDatBanRepository = phieuDatBanRepository;
        this.chiTietDatBanRepository = chiTietDatBanRepository;
        this.banRepository = banRepository;
        this.phieuDatBanMapper = phieuDatBanMapper;
        this.banMapper = banMapper;
        this.hoaDonRepository = hoaDonRepository;
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
            banResponses.add(banMapper.toResponse(ban));
        }

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
        PhieuDatBan phieu = phieuDatBanRepository.findActiveById(idPhieu)
                .orElseThrow(() -> new ResourceNotFoundException("Phiếu không tồn tại hoặc đã được xử lý"));

        boolean daCoHoaDon = hoaDonRepository.existsByPhieuDatBan_IdPhieuDatAndThoiGianXoa(idPhieu, 0L);

        if (daCoHoaDon) {
            throw new BadRequestException("Không thể hủy phiếu vì đã có hóa đơn được tạo. Hãy xử lý hóa đơn trước!");
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
        PhieuDatBan phieu = phieuDatBanRepository.findActiveById(idPhieu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu đặt để hoàn tất"));

        phieu.setTrangThaiDat(TrangThaiDatBan.HOAN_TAT);
        phieuDatBanRepository.save(phieu);

        List<ChiTietDatBan> chiTiets = chiTietDatBanRepository.findByPhieuDatBan_IdPhieuDat(idPhieu);

        for (ChiTietDatBan ct : chiTiets) {
            Ban ban = ct.getBan();
            ban.setTinhTrangBan(TinhTrangBan.TRONG);
            banRepository.save(ban);
        }
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

        for (ChiTietDatBan ct : chiTiets) {
            Ban ban = ct.getBan();
            ban.setTinhTrangBan(TinhTrangBan.CO_KHACH);
            banRepository.save(ban);
            banResponses.add(banMapper.toResponse(ban));
        }

        return setBanToResponse(phieuDatBanMapper.toResponse(phieu), banResponses);
    }
}