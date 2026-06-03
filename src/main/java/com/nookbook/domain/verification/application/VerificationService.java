package com.nookbook.domain.verification.application;

import com.nookbook.domain.mail.application.MailService;
import com.nookbook.domain.mail.dto.req.SendCodeReq;
import com.nookbook.domain.verification.dto.req.VerificationCodeReq;
import com.nookbook.domain.verification.exception.VerificationCodeExpiredException;
import com.nookbook.domain.verification.exception.VerificationCodeMismatchException;
import com.nookbook.global.payload.Message;
import com.nookbook.global.util.RandomCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VerificationService {

    private static final String REDIS_KEY_PREFIX = "verification:email:";
    private static final String VERIFIED_KEY_PREFIX = "verification:verified:";
    private static final int CODE_LENGTH = 6;
    private static final long CODE_TTL_MINUTES = 5;
    private static final long VERIFIED_TTL_MINUTES = 10;

    private final RedisTemplate<String, Object> redisTemplate;
    private final MailService mailService;

    public ResponseEntity<Message> sendVerificationCode(SendCodeReq sendCodeReq) {
        String email = sendCodeReq.getEmail();
        String code = RandomCodeGenerator.generateCode(CODE_LENGTH);

        String redisKey = REDIS_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(redisKey, code, CODE_TTL_MINUTES, TimeUnit.MINUTES);

        mailService.sendPasswordResetVerificationEmail(email, code);

        return ResponseEntity.ok(new Message("인증 코드가 이메일로 발송되었습니다."));
    }

    public ResponseEntity<Message> verifyCode(VerificationCodeReq verificationCodeReq) {
        String email = verificationCodeReq.getEmail();
        String code = verificationCodeReq.getCode();

        String redisKey = REDIS_KEY_PREFIX + email;
        Object storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            throw new VerificationCodeExpiredException();
        }

        if (!storedCode.toString().equals(code)) {
            throw new VerificationCodeMismatchException();
        }

        redisTemplate.delete(redisKey);

        String verifiedKey = VERIFIED_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(verifiedKey, "true", VERIFIED_TTL_MINUTES, TimeUnit.MINUTES);

        return ResponseEntity.ok(new Message("인증이 완료되었습니다."));
    }

    public boolean isVerified(String email) {
        String verifiedKey = VERIFIED_KEY_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(verifiedKey));
    }

    public void removeVerified(String email) {
        String verifiedKey = VERIFIED_KEY_PREFIX + email;
        redisTemplate.delete(verifiedKey);
    }
}
