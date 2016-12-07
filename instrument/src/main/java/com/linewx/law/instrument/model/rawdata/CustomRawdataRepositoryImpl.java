package com.linewx.law.instrument.model.rawdata;

import java.util.List;
package net.topikachu.mqtt2db.jpa.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by 禕 on 2016/12/5.
 */
public class CustomRawdataRepositoryImpl implements CustomRawdataRepository {


    @Autowired
    private EntityManager entityManager;

    @Autowired
    private Log log;

    /*@Override
    public List<SampleEntity> findAll(String deviceId, Long start, Long end, Integer limit) {
        QSampleEntity sampleEntity = QSampleEntity.sampleEntity;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (deviceId != null) {
            booleanBuilder.and(sampleEntity.deviceId.eq(deviceId));
        }
        if (start != null) {
            booleanBuilder.and(sampleEntity.timestamp.gt(start));
        }
        if (end != null) {
            booleanBuilder.and(sampleEntity.timestamp.lt(end));
        }

        limit = limit == null ? 10 : limit;
        JPAQuery<?> query = new JPAQuery<SampleEntity>(entityManager);
        return query.select(sampleEntity)
                .from(sampleEntity)
                .where(booleanBuilder)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<String> findExistingMessageIds(List<String> ids) {
        QSampleEntity sampleEntity = QSampleEntity.sampleEntity;
        JPAQuery<?> query = new JPAQuery<SampleEntity>(entityManager);
        return query.select(sampleEntity.messageId)
                .from(sampleEntity)
                .where(sampleEntity.messageId.in(ids))
                .fetch();

    }*/
}
