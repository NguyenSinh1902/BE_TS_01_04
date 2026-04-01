package iuh.fit.se.service.impl;

import iuh.fit.se.dto.nhatkyai.NhatKyAiRequest;
import iuh.fit.se.dto.nhatkyai.NhatKyAiResponse;
import iuh.fit.se.entity.NhanVien;
import iuh.fit.se.entity.NhatKyAi;
import iuh.fit.se.exception.ResourceNotFoundException;
import iuh.fit.se.mapper.NhatKyAiMapper;
import iuh.fit.se.repository.NhanVienRepository;
import iuh.fit.se.repository.NhatKyAiRepository;
import iuh.fit.se.service.NhatKyAiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NhatKyAiServiceImpl implements NhatKyAiService {

    private final NhatKyAiRepository nhatKyAiRepository;
    private final NhanVienRepository nhanVienRepository;
    private final NhatKyAiMapper nhatKyAiMapper;

    // Constructor
    public NhatKyAiServiceImpl(NhatKyAiRepository nhatKyAiRepository,
                               NhanVienRepository nhanVienRepository,
                               NhatKyAiMapper nhatKyAiMapper) {
        this.nhatKyAiRepository = nhatKyAiRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.nhatKyAiMapper = nhatKyAiMapper;
    }

    @Override
    @Transactional
    public NhatKyAiResponse luuNhatKy(NhatKyAiRequest request) {

        NhanVien quanLy = nhanVienRepository.findById(request.idQuanLy())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên quản lý"));

        NhatKyAi nhatKy = nhatKyAiMapper.toEntity(request);
        nhatKy.setQuanLy(quanLy);

        NhatKyAi saved = nhatKyAiRepository.save(nhatKy);

        return nhatKyAiMapper.toResponse(saved);
    }

    @Override
    public List<NhatKyAiResponse> layLichSuTheoQuanLy(Integer idQuanLy) {
        if (!nhanVienRepository.existsById(idQuanLy)) {
            throw new ResourceNotFoundException("Nhân viên quản lý không tồn tại");
        }

        return nhatKyAiRepository.findByQuanLy_IdNhanVienOrderByThoiGianTaoDesc(idQuanLy)
                .stream()
                .map(nhatKyAiMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NhatKyAiResponse layChiTiet(Integer idNhatKy) {
        return nhatKyAiRepository.findById(idNhatKy)
                .map(nhatKyAiMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhật ký AI với ID: " + idNhatKy));
    }
}