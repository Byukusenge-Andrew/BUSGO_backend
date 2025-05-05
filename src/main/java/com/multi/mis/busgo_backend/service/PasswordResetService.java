package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.PasswordResetToken;
import com.multi.mis.busgo_backend.model.User;
import com.multi.mis.busgo_backend.repository.PasswordResetTokenRepository;
import com.multi.mis.busgo_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class PasswordResetService {
    private static final Logger logger = Logger.getLogger(PasswordResetService.class.getName());
    private static final long TOKEN_VALIDITY_MINUTES = 60;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Transactional
    public void createPasswordResetToken(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found with email: " + email));

        tokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES);

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        tokenRepository.save(resetToken);

        sendResetEmail(user.getEmail(), token);
    }

    private void sendResetEmail(String email, String token) throws MessagingException {
        String resetUrl = "http://localhost:4200/reset-password?token=" + token;
        String subject = "Password Reset Request";
        String content = "<html><body style='font-family: Arial, sans-serif;'>"
                + "<h2>Password Reset Request</h2>"
                + "<p>You have requested to reset your password for BUSGO.</p>"
                + "<p>Click the button below to reset your password:</p>"
                + "<a href=\"" + resetUrl + "\" style='display: inline-block; padding: 10px 20px; color: white; background-color: #ff4c30; text-decoration: none; border-radius: 5px;'>Reset Password</a>"
                + "<p>This link will expire in " + TOKEN_VALIDITY_MINUTES + " minutes.</p>"
                + "<p>If you did not request a password reset, please ignore this email or contact support.</p>"
                + "<p>Best regards,<br>The BUSGO Team</p>"
                + "</body></html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("noreply@test-zkq340ee7z6gd796.mlsender.net"); // Verified sender email
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
        logger.info("Password reset email sent to: " + email);
    }
    @Transactional
    public void resetPassword(String token, String newPassword) throws Exception {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new Exception("Invalid or expired token"));

        if (resetToken.isExpired()) {
            throw new Exception("Token has expired or already used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        logger.info("Password reset successfully for user: " + user.getEmail());
    }
}