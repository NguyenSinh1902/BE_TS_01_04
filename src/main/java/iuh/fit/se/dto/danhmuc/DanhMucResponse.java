package iuh.fit.se.dto.danhmuc;

public record DanhMucResponse(
        Integer idDanhMuc,
        String tenDanhMuc,
        String moTa,
        String duongDanAnh,
        Boolean laHeThong
) {}