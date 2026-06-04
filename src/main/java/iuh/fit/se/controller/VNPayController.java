package iuh.fit.se.controller;

import iuh.fit.se.config.vnpay.VNPayConfig;
import iuh.fit.se.entity.HoaDon;
import iuh.fit.se.enums.TrangThaiHoaDon;
import iuh.fit.se.repository.HoaDonRepository;
import iuh.fit.se.service.FirebaseMessagingService;
import iuh.fit.se.service.FirebaseRealtimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {

    private final HoaDonRepository hoaDonRepository;
    private final VNPayConfig vnPayConfig;
    private final FirebaseRealtimeService firebaseRealtimeService;
    private final FirebaseMessagingService firebaseMessagingService;

    public VNPayController(HoaDonRepository hoaDonRepository, VNPayConfig vnPayConfig, FirebaseRealtimeService firebaseRealtimeService, FirebaseMessagingService firebaseMessagingService) {
        this.hoaDonRepository = hoaDonRepository;
        this.vnPayConfig = vnPayConfig;
        this.firebaseRealtimeService = firebaseRealtimeService;
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @GetMapping("/create-payment-url")
    public ResponseEntity<Map<String, String>> createPaymentUrl(@RequestParam Integer idHoaDon, @RequestParam long amount) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = String.valueOf(idHoaDon);
        String vnp_IpAddr = "127.0.0.1"; // Trong thực tế lấy từ HttpServletRequest
        String vnp_TmnCode = vnPayConfig.getVnp_TmnCode();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPAY nhận số tiền x100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + idHoaDon);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnp_ReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build chuỗi băm
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getVnp_HashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(@RequestParam Map<String, String> params) {
        Map<String, String> response = new HashMap<>();
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");
            params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");

            // Kiểm tra chữ ký
            String signValue = vnPayConfig.hashAllFields(params);
            if (!signValue.equals(vnp_SecureHash)) {
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
                return ResponseEntity.ok(response);
            }

            System.out.println(">>> [VNPAY IPN] Received request with params: " + params);

            String txnRef = params.get("vnp_TxnRef");
            String responseCode = params.get("vnp_ResponseCode");
            System.out.println(">>> [VNPAY IPN] idHoaDon=" + txnRef + " | responseCode=" + responseCode);
            Integer idHoaDon = Integer.parseInt(txnRef);

            Optional<HoaDon> hdOpt = hoaDonRepository.findById(idHoaDon);
            if (hdOpt.isPresent()) {
                HoaDon hd = hdOpt.get();
                if ("00".equals(responseCode)) {
                    if (hd.getTrangThai() == TrangThaiHoaDon.DANG_CHO_VNPAY) {
                        hd.setTrangThai(TrangThaiHoaDon.CHO_XAC_NHAN);
                        hd.setThoiGianThanhToan(LocalDateTime.now());
                        HoaDon savedHd = hoaDonRepository.save(hd);
                        
                        firebaseRealtimeService.updateOrderRealtime(savedHd);
                        try {
                            firebaseMessagingService.sendNotificationToTopic("THU_NGAN", "🔔 Đơn VNPAY Mới!", "Có đơn đặt hàng Online vừa thanh toán thành công.");
                        } catch (Exception ignored) {}

                        response.put("RspCode", "00");
                        response.put("Message", "Confirm Success");
                    } else {
                        response.put("RspCode", "02");
                        response.put("Message", "Order already confirmed");
                    }
                } else {
                    hd.setTrangThai(TrangThaiHoaDon.DA_HUY);
                    HoaDon savedHd = hoaDonRepository.save(hd);
                    
                    firebaseRealtimeService.updateOrderRealtime(savedHd);

                    response.put("RspCode", "00");
                    response.put("Message", "Order Canceled");
                }
            } else {
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
            }
        } catch (Exception e) {
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<Map<String, String>> vnpayReturn(@RequestParam Map<String, String> params) {
        Map<String, String> response = new HashMap<>();
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");

            // Tạo bản copy để tính hash (không được sửa params gốc trước khi lấy hash)
            Map<String, String> paramsToHash = new HashMap<>(params);
            paramsToHash.remove("vnp_SecureHash");
            paramsToHash.remove("vnp_SecureHashType");

            String signValue = vnPayConfig.hashAllFields(paramsToHash);
            String txnRef = params.get("vnp_TxnRef");
            String responseCode = params.get("vnp_ResponseCode");

            System.out.println(">>> [VNPAY RETURN] idHoaDon=" + txnRef + " | responseCode=" + responseCode);

            if (!signValue.equals(vnp_SecureHash)) {
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
                return ResponseEntity.ok(response);
            }

            Integer idHoaDon = Integer.parseInt(txnRef);
            Optional<HoaDon> hdOpt = hoaDonRepository.findById(idHoaDon);

            if (hdOpt.isPresent()) {
                HoaDon hd = hdOpt.get();
                if ("00".equals(responseCode)) {
                    if (hd.getTrangThai() == TrangThaiHoaDon.DANG_CHO_VNPAY) {
                        hd.setTrangThai(TrangThaiHoaDon.CHO_XAC_NHAN);
                        hd.setThoiGianThanhToan(LocalDateTime.now());
                        HoaDon savedHd = hoaDonRepository.save(hd);

                        firebaseRealtimeService.updateOrderRealtime(savedHd);
                        try {
                            firebaseMessagingService.sendNotificationToTopic("THU_NGAN", "🔔 Đơn VNPAY Mới!", "Có đơn đặt hàng Online vừa thanh toán thành công.");
                        } catch (Exception ignored) {}

                        System.out.println(">>> [VNPAY RETURN] Đơn #" + idHoaDon + " -> CHO_XAC_NHAN");
                    }
                    response.put("RspCode", "00");
                    response.put("Message", "Payment Success");
                } else {
                    if (hd.getTrangThai() == TrangThaiHoaDon.DANG_CHO_VNPAY) {
                        hd.setTrangThai(TrangThaiHoaDon.DA_HUY);
                        HoaDon savedHd = hoaDonRepository.save(hd);
                        firebaseRealtimeService.updateOrderRealtime(savedHd);
                    }
                    response.put("RspCode", responseCode);
                    response.put("Message", "Payment Failed");
                }
            } else {
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
            }
        } catch (Exception e) {
            System.err.println(">>> [VNPAY RETURN] Error: " + e.getMessage());
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }
        return ResponseEntity.ok(response);
    }
}
