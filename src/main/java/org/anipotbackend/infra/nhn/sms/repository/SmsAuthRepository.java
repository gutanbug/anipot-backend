package org.anipotbackend.infra.nhn.sms.repository;

import org.anipotbackend.infra.nhn.sms.model.SmsType;
import org.anipotbackend.infra.nhn.sms.model.entity.SmsAuth;
import org.anipotbackend.infra.nhn.sms.repository.custom.SmsAuthCustomRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmsAuthRepository extends MongoRepository<SmsAuth, String>, SmsAuthCustomRepository {
}
