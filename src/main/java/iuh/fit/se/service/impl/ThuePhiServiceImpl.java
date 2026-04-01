package iuh.fit.se.service.impl;

import iuh.fit.se.dto.thuephi.ThuePhiRequest;
import iuh.fit.se.dto.thuephi.ThuePhiResponse;
import iuh.fit.se.entity.ThuePhi;
import iuh.fit.se.exception.NotFoundException;
import iuh.fit.se.mapper.ThuePhiMapper;
import iuh.fit.se.repository.ThuePhiRepository;
import iuh.fit.se.service.ThuePhiService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThuePhiServiceImpl implements ThuePhiService {

    private final ThuePhiRepository thuePhiRepository;
    private final ThuePhiMapper thuePhiMapper;

    public ThuePhiServiceImpl(ThuePhiRepository thuePhiRepository, ThuePhiMapper thuePhiMapper) {
        this.thuePhiRepository = thuePhiRepository;
        this.thuePhiMapper = thuePhiMapper;
    }

    @Override
    public List<ThuePhiResponse> layTatCa() {
        return thuePhiRepository.findAll().stream()
                .map(thuePhiMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ThuePhiResponse taoMoi(ThuePhiRequest request) {
        ThuePhi thuePhi = thuePhiMapper.toEntity(request);
        return thuePhiMapper.toResponse(thuePhiRepository.save(thuePhi));
    }

    @Override
    @Transactional
    public ThuePhiResponse capNhat(Integer id, ThuePhiRequest request) {
        ThuePhi thuePhi = thuePhiRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy Thuế/Phí"));

        thuePhi.setTenThuePhi(request.tenThuePhi());
        thuePhi.setGiaTri(request.giaTri());
        thuePhi.setLaMacDinh(request.laMacDinh());

        return thuePhiMapper.toResponse(thuePhiRepository.save(thuePhi));
    }

    @Override
    @Transactional
    public void xoa(Integer id) {
        ThuePhi thuePhi = thuePhiRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy Thuế/Phí"));
        thuePhi.setThoiGianXoa(System.currentTimeMillis());
        thuePhiRepository.save(thuePhi);
    }
}
