package iuh.fit.se.service.impl;

import iuh.fit.se.dto.ban.BanRequest;
import iuh.fit.se.dto.ban.BanResponse;
import iuh.fit.se.entity.Ban;
import iuh.fit.se.enums.TinhTrangBan;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.mapper.BanMapper;
import iuh.fit.se.repository.BanRepository;
import iuh.fit.se.service.BanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BanServiceImpl implements BanService {

    private final BanRepository banRepository;
    private final BanMapper banMapper;

    public BanServiceImpl(BanRepository banRepository, BanMapper banMapper) {
        this.banRepository = banRepository;
        this.banMapper = banMapper;
    }

    private Ban findActiveBan(Integer id) {
        return banRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bàn không tồn tại hoặc đã bị xóa!"));
    }

    @Override
    public List<BanResponse> layTatCaBan() {
        return banRepository.findByThoiGianXoaOrderByTenBanAsc(0L)
                .stream()
                .map(banMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BanResponse taoMoi(BanRequest request) {
        if (banRepository.existsByTenBanAndThoiGianXoa(request.tenBan(), 0L)) {
            throw new BadRequestException("Tên bàn '" + request.tenBan() + "' đã tồn tại!");
        }
        Ban ban = banMapper.toEntity(request);
        ban.setTinhTrangBan(TinhTrangBan.TRONG);
        return banMapper.toResponse(banRepository.save(ban));
    }

    @Override
    @Transactional
    public BanResponse capNhat(Integer id, BanRequest request) {
        Ban ban = findActiveBan(id);
        if (!ban.getTenBan().equals(request.tenBan()) &&
                banRepository.existsByTenBanAndThoiGianXoa(request.tenBan(), 0L)) {
            throw new BadRequestException("Tên bàn mới đã tồn tại!");
        }

        if (ban.getTinhTrangBan() != TinhTrangBan.TRONG) {
            throw new BadRequestException("Bàn đang phục vụ, không thể sửa thông tin!");
        }

        ban.setTenBan(request.tenBan());
        ban.setSucChua(request.sucChua());
        return banMapper.toResponse(banRepository.save(ban));
    }

    @Override
    @Transactional
    public void xoa(Integer id) {
        Ban ban = findActiveBan(id);
        if (ban.getTinhTrangBan() != TinhTrangBan.TRONG) {
            throw new BadRequestException("Bàn đang bận, không thể xóa!");
        }
        ban.setThoiGianXoa(System.currentTimeMillis());
        banRepository.save(ban);
    }
}