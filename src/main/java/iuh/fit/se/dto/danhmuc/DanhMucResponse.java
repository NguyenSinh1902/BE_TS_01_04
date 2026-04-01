package iuh.fit.se.dto.danhmuc;

public record DanhMucResponse(
        Integer idDanhMuc,
        String tenDanhMuc,
        String duongDanAnh,
        Boolean laHeThong
) {}