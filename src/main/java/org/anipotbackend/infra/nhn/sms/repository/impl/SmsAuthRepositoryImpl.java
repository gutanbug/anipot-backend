package org.anipotbackend.infra.nhn.sms.repository.impl;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.anipotbackend.global.error.exception.NotSingleObjectException;
import org.anipotbackend.infra.nhn.sms.exception.SmsAuthNotFoundException;
import org.anipotbackend.infra.nhn.sms.model.SmsStatus;
import org.anipotbackend.infra.nhn.sms.model.SmsType;
import org.anipotbackend.infra.nhn.sms.model.entity.SmsAuth;
import org.anipotbackend.infra.nhn.sms.repository.custom.SmsAuthCustomRepository;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class SmsAuthRepositoryImpl implements SmsAuthCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<SmsAuth> findValidSmsAuth(@Param("phone") String phoneNumber, @Param("type") SmsType smsType) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("phone").is(phoneNumber),
                Criteria.where("type").is(smsType)
        );

        List<SmsAuth> maybeValidSmsAuth = mongoTemplate
                .find(new Query(criteria), SmsAuth.class);

        if(!maybeValidSmsAuth.isEmpty()) {
            List<SmsAuth> smsAuths = maybeValidSmsAuth.stream().toList();
            updateExpired(maybeValidSmsAuth.stream().filter(SmsAuth::isExpired).toList());

            if (smsAuths.size() > 1)   throw new NotSingleObjectException();
            else if (smsAuths.size() == 1) {
                return Optional.of(smsAuths.get(0));
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public void updateCode(String phone, SmsType type, String code, int trial) {
        Update update = new Update();
        update.set("code", code);
        update.set("trial", trial + 1);
        update.set("status", SmsStatus.UNCHECKED);
        update.set("expiration", LocalDateTime.now().plusSeconds(type.getExpirationSecond()));
        update.set("last_modified", LocalDateTime.now());

        // 조건에 맞는 문서를 업데이트
        Query query = new Query(Criteria.where("phone").is(phone)
                .and("type").is(type));

        UpdateResult result = mongoTemplate.updateFirst(query, update, SmsAuth.class);

        if (result.getMatchedCount() == 0) {
            throw new SmsAuthNotFoundException();
        }
    }

    @Override
    public Optional<SmsAuth> getSmsAuth(String phoneNumber, SmsType type) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("phone").is(phoneNumber),
                Criteria.where("type").is(type)
        );

        List<SmsAuth> maybeValidSmsAuth = mongoTemplate
                .find(new Query(criteria), SmsAuth.class);

        if(!maybeValidSmsAuth.isEmpty()) {
            List<SmsAuth> aliveSmsAuths = maybeValidSmsAuth.stream().filter(SmsAuth::isAlive).toList();

            if (aliveSmsAuths.size() > 1)   throw new NotSingleObjectException();
            else if (aliveSmsAuths.size() == 1)    return Optional.of(aliveSmsAuths.get(0));
            return Optional.empty();
        }
        return Optional.empty();
    }

    private void updateExpired(List<SmsAuth> expiredSmsAuths) {
        if (expiredSmsAuths.isEmpty()) return;
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, SmsAuth.class);
        expiredSmsAuths.stream()
                .filter(SmsAuth::isExpired)
                .forEach(smsAuth -> {
                    Query findObjectById = new Query(Criteria.where("id").is(smsAuth.getId()));
                    bulkOps.updateOne(findObjectById, Update.update("status", SmsStatus.EXPIRED));
                });
        bulkOps.execute();
    }

    // 테스트를 위한 메소드
    public void expiredSmsAuth(SmsAuth smsAuth) {
        Update update = new Update();
        update.set("expiration", LocalDateTime.now().minusMinutes(10));

        Query query = new Query(Criteria.where("phone").is(smsAuth.getPhone())
                .and("type").is(smsAuth.getSmsType()));

        UpdateResult result = mongoTemplate.updateFirst(query, update, SmsAuth.class);

        if (result.getMatchedCount() == 0) {
            throw new RuntimeException();
        }
    }

    @Override
    public Optional<SmsAuth> findSmsAuthByCodeForTest(@Param("code") String code) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("code").is(code),
                Criteria.where("status").is(SmsStatus.CHECKED)
        );

        List<SmsAuth> maybeValidSmsAuth = mongoTemplate
                .find(new Query(criteria), SmsAuth.class);

        if(!maybeValidSmsAuth.isEmpty()) {
            List<SmsAuth> aliveSmsAuths = maybeValidSmsAuth.stream().filter(SmsAuth::isAlive).toList();

            if (aliveSmsAuths.size() > 1)   throw new NotSingleObjectException();
            else if (aliveSmsAuths.size() == 1)    return Optional.of(aliveSmsAuths.get(0));
            return Optional.empty();
        }
        return Optional.empty();
    }
}
