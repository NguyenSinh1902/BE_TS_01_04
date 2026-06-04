package iuh.fit.se.mapper;

import iuh.fit.se.dto.bienthe.BienTheRequest;
import iuh.fit.se.dto.bienthe.BienTheResponse;
import iuh.fit.se.entity.BienTheSanPham;
import iuh.fit.se.entity.DinhLuongMonAn;
import iuh.fit.se.entity.NguyenLieu;
import iuh.fit.se.repository.DinhLuongMonAnRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class BienTheMapper {

    @Autowired
    protected DinhLuongMonAnRepository dinhLuongMonAnRepository;

    @Mapping(target = "soLuongTonKho", expression = "java(tinhSoLuongLyConLai(entity))")
    public abstract BienTheResponse toResponse(BienTheSanPham entity);

    @Mapping(target = "idBienThe", ignore = true)
    @Mapping(target = "sanPham", ignore = true)
    @Mapping(target = "thoiGianXoa", constant = "0L")
    public abstract BienTheSanPham toEntity(BienTheRequest request);

    protected Integer tinhSoLuongLyConLai(BienTheSanPham entity) {
        if (entity.getIdBienThe() == null || dinhLuongMonAnRepository == null) {
            return entity.getSoLuongTonKho();
        }
        
        List<DinhLuongMonAn> dinhLuongs = dinhLuongMonAnRepository.findByBienThe_IdBienThe(entity.getIdBienThe());
        if (dinhLuongs == null || dinhLuongs.isEmpty()) {
            return entity.getSoLuongTonKho(); // Trả về số tĩnh nếu không có công thức
        }

        int soLyToiDa = Integer.MAX_VALUE;
        for (DinhLuongMonAn dl : dinhLuongs) {
            NguyenLieu nl = dl.getNguyenLieu();
            if (dl.getSoLuongTieuHao().compareTo(BigDecimal.ZERO) <= 0) continue;
            
            int phanChia = nl.getSoLuongTon().divide(dl.getSoLuongTieuHao(), 0, RoundingMode.DOWN).intValue();
            if (phanChia < soLyToiDa) {
                soLyToiDa = phanChia;
            }
        }
        return soLyToiDa == Integer.MAX_VALUE ? entity.getSoLuongTonKho() : soLyToiDa;
    }
}
