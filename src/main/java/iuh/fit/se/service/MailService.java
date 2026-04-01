package iuh.fit.se.service;

public interface MailService {
    void guiOtp(String to, String otp);
    void guiThongBaoDangKy(String to, String hoTen);
}