package iuh.fit.se.service.impl;

import iuh.fit.se.service.MailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void guiOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mã OTP xác thực - Milk Tea Shop");
        message.setText("Mã OTP của bạn là: " + otp + "\nHiệu lực trong 5 phút. Vui lòng không cung cấp mã này cho bất kỳ ai.");
        mailSender.send(message);
    }

    @Override
    public void guiThongBaoDangKy(String to, String hoTen) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Chào mừng thành viên mới - Milk Tea Shop");
        message.setText("Chào " + hoTen + ",\nTài khoản của bạn đã được khởi tạo thành công và đang chờ Admin phê duyệt.");
        mailSender.send(message);
    }
}