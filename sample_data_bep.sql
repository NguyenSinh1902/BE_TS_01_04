-- 1. XÓA SẠCH DỮ LIỆU CŨ ĐỂ KHÔNG BỊ TRÙNG LẶP (Chạy từng dòng nếu bị lỗi khóa ngoại)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE dinh_luong_mon_an;
TRUNCATE TABLE recipe_chuyen_doi;
TRUNCATE TABLE nguyen_lieu;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. NHẬP KHO NGUYÊN LIỆU (THÔ & BÁN THÀNH PHẨM)
INSERT INTO nguyen_lieu (id_nguyen_lieu, ten_nguyen_lieu, so_luong_ton, don_vi_tinh, loai_nguyen_lieu, gia_nhap_don_vi, thoi_gian_xoa) VALUES 
-- Nhóm Nguyên liệu thô (Pha tới đâu trừ tới đó, hoặc dùng để nấu mẻ)
(1, 'Trà Xanh Thái Nguyên (Thô)', 50000, 'g', 'NGUYEN_LIEU_THO', 250.00, 0),
(2, 'Trà Ô Long (Thô)', 50000, 'g', 'NGUYEN_LIEU_THO', 400.00, 0),
(3, 'Bột Cà Phê Nguyên Chất', 20000, 'g', 'NGUYEN_LIEU_THO', 300.00, 0),
(4, 'Sữa Tươi Thanh Trùng', 100000, 'ml', 'NGUYEN_LIEU_THO', 40.00, 0),
(5, 'Sữa Đặc Ngôi Sao', 50000, 'ml', 'NGUYEN_LIEU_THO', 60.00, 0),
(6, 'Trân Châu Đen Sống', 20000, 'g', 'NGUYEN_LIEU_THO', 50.00, 0),
(7, 'Hộp Trân Châu Trắng (Ăn liền)', 10000, 'g', 'TOPPING_AN_LIEN', 150.00, 0),

-- Nhóm Bán thành phẩm (Phải qua chế biến mới ra được)
(8, 'Cốt Trà Sữa Matcha', 0, 'ml', 'BAN_THANH_PHAM', 0.00, 0),
(9, 'Cốt Trà Sữa Ô Long', 0, 'ml', 'BAN_THANH_PHAM', 0.00, 0),
(10, 'Trân Châu Đen Đã Luộc', 0, 'g', 'BAN_THANH_PHAM', 0.00, 0);


-- 3. CÀI ĐẶT CÔNG THỨC SƠ CHẾ (Nấu theo mẻ)
INSERT INTO recipe_chuyen_doi (id_ban_thanh_pham, id_nguyen_lieu_tho, ham_luong_can, so_luong_thu_duoc) VALUES 
-- Mẻ 1: Nấu 2500ml Cốt Trà Matcha tốn 100g Trà Xanh + 400ml Sữa Tươi
(8, 1, 100.00, 2500.00),
(8, 4, 400.00, 2500.00),

-- Mẻ 2: Nấu 2500ml Cốt Trà Ô Long tốn 120g Trà Ô Long + 400ml Sữa Tươi
(9, 2, 120.00, 2500.00),
(9, 4, 400.00, 2500.00),

-- Mẻ 3: Luộc 1000g Trân châu chín thì tốn 500g Trân châu sống (Nở ra)
(10, 6, 500.00, 1000.00);


-- 4. CÀI ĐẶT ĐỊNH LƯỢNG CHO TỪNG SIZE NƯỚC (Bán cho khách)
INSERT INTO dinh_luong_mon_an (id_bien_the, id_nguyen_lieu, so_luong_tieu_hao) VALUES 
-- Trà Sữa Matcha (Trừ vào kho Bán thành phẩm: Cốt Matcha ID 8)
(1, 8, 200.00), -- Size M (idBienThe=1) tốn 200ml Cốt Matcha
(2, 8, 270.00), -- Size L (idBienThe=2) tốn 270ml
(3, 8, 350.00), -- Size XL (idBienThe=3) tốn 350ml

-- Trà Sữa Ô Long (Trừ vào kho Bán thành phẩm: Cốt Ô Long ID 9)
(4, 9, 200.00), -- Size M (idBienThe=4)
(5, 9, 270.00), -- Size L (idBienThe=5)
(6, 9, 350.00), -- Size XL (idBienThe=6)

-- Cà Phê Đen Đá (TRỪ THẲNG VÀO KHO THÔ vì pha máy trực tiếp)
(34, 3, 25.00), -- Cà phê đen Size M (idBienThe=34) tốn 25g Bột cà phê ID 3

-- Cà Phê Sữa Đá (Pha trực tiếp tốn Bột cà phê + Sữa đặc)
(33, 3, 20.00), -- Cà phê sữa Size M tốn 20g Bột cà phê ID 3
(33, 5, 40.00), -- Cà phê sữa Size M tốn 40ml sữa đặc ID 5

-- Topping Trân Châu Đen (Trừ vào kho Bán thành phẩm: Đã luộc ID 10)
(49, 10, 50.00), 

-- Topping Trân Châu Trắng (TRỪ THẲNG VÀO KHO THÔ vì múc trực tiếp từ hộp ID 7)
(50, 7, 50.00);
