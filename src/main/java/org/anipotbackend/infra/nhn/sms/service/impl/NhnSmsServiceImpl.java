package org.anipotbackend.infra.nhn.sms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anipotbackend.domain.user.model.UserStatus;
import org.anipotbackend.domain.user.model.entity.User;
import org.anipotbackend.domain.user.repository.UserRepository;
import org.anipotbackend.infra.nhn.sms.exception.CannotSendSmsException;
import org.anipotbackend.infra.nhn.sms.exception.NotValidSmsResponseException;
import org.anipotbackend.infra.nhn.sms.exception.TooManyFailedSmsRequestException;
import org.anipotbackend.infra.nhn.sms.model.SmsStatus;
import org.anipotbackend.infra.nhn.sms.model.dto.request.NHNCloudSmsRequest;
import org.anipotbackend.infra.nhn.sms.model.dto.response.NHNCloudSmsResponse;
import org.anipotbackend.infra.nhn.sms.model.entity.SmsAuth;
import org.anipotbackend.infra.nhn.sms.repository.SmsAuthRepository;
import org.anipotbackend.infra.nhn.sms.service.NhnSmsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.anipotbackend.infra.nhn.sms.model.dto.response.NHNCloudSmsResponse.Body.Data.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NhnSmsServiceImpl implements NhnSmsService {

    private static final int MAX_TRIAL = 5;

    private final WebClient webClient;

    @Value("${nhn.sms.api-path}")
    private final String apiPath;

    @Value("${nhn.sms.secret-key}")
    private final String secretKey;

    @Value("${nhn.sms.sender-phone}")
    private final String senderPhone;

    private final SmsAuthRepository smsAuthRepository;
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;

    @Override
    public void sendSms(String phone, String smsVerificationCode) {
        NHNCloudSmsRequest request = new NHNCloudSmsRequest(senderPhone, phone, smsVerificationCode);

        NHNCloudSmsResponse response = null;
        try {
            response = webClient.post()
                    .uri(apiPath)
                    .header("X-Secret-Key", secretKey)
                    .header("Content-Type", "application/json")
                    .body(Mono.just(request), NHNCloudSmsRequest.class)
                    .retrieve()
                    .bodyToMono(NHNCloudSmsResponse.class)
                    .block();

            validateResponse(response);
        } catch (WebClientResponseException e) {
            throw new CannotSendSmsException();
        } catch (NotValidSmsResponseException e) {
            log.debug(String.format("Error while sending SMS to %s: %s", phone, senderPhone));
            throw e;
        }
        log.info("Code " + smsVerificationCode + "sent to " + phone);
    }

    private static void validateResponse(NHNCloudSmsResponse response) {
        if (response == null || response.getHeader() == null || response.getBody() == null) {
            throw new NotValidSmsResponseException();
        } else {
            NHNCloudSmsResponse.Header header = response.getHeader();
            if (!header.getIsSuccessful()) {
                throw new NotValidSmsResponseException();
            }

            List<SendResult> results = Optional.of(response)
                    .map(NHNCloudSmsResponse::getBody)
                    .map(NHNCloudSmsResponse.Body::getData)
                    .map(NHNCloudSmsResponse.Body.Data::getSendResultList)
                    .orElse(null);

            if (results == null) {
                throw new NotValidSmsResponseException();
            } else {
                for (SendResult result : results) {
                    if (result.getResultCode() != 0) {
                        throw new NotValidSmsResponseException();
                    }
                }
            }
        }
    }

    @Transactional
    @Override
    public void saveOrUpdate(SmsAuth smsAuth) {
        Optional<SmsAuth> maybeSmsAuth = smsAuthRepository.findValidSmsAuth(smsAuth.getPhone(), smsAuth.getSmsType());
        if (maybeSmsAuth.isEmpty()) {
            smsAuthRepository.save(smsAuth);
            return;
        }

        SmsAuth referredSmsAuth = maybeSmsAuth.get();

        if (referredSmsAuth.getSmsStatus() == SmsStatus.BANNED) {
            throw new TooManyFailedSmsRequestException();
        }

        if (referredSmsAuth.getTrial() >= MAX_TRIAL) {
            referredSmsAuth.changeStatus(SmsStatus.BANNED);
            banPhoneAndUser(referredSmsAuth.getPhone());
            throw new TooManyFailedSmsRequestException();
        }

        checkOver12Hours(referredSmsAuth);
        smsAuthRepository.updateCode(referredSmsAuth.getPhone(), smsAuth.getSmsType(), smsAuth.getAuthCode(), referredSmsAuth.getTrial());
    }

    private static void checkOver12Hours(SmsAuth referredSmsAuth) {
        if (referredSmsAuth.getLastModified().plusHours(12).isBefore(LocalDateTime.now())) {
            referredSmsAuth.clearTrial();
        }
    }

    protected void banPhoneAndUser(String phone) {
        Optional<User> maybeUser = userRepository.findByPhone(phone);

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            user.changeStatus(UserStatus.BANNED);
        }
    }

    @Override
    public void updateToChecked(SmsAuth smsAuth) {
        smsAuth.changeStatus(SmsStatus.CHECKED);
        Query query = new Query(
                Criteria.where("phone").is(smsAuth.getPhone())
                        .and("smsType").is(smsAuth.getSmsType())
        );
        mongoTemplate.updateFirst(
                query,
                new Update().set("status", smsAuth.getSmsStatus()),
                SmsAuth.class
        );
    }
}
