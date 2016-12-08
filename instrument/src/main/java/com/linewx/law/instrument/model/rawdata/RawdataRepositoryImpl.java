package com.linewx.law.instrument.model.rawdata;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 禕 on 2016/12/5.
 */
public class RawdataRepositoryImpl implements CustomRawdataRepository {


    @Autowired
    private EntityManager entityManager;

    private static final String QUERY_IDS_BY_ROW_NUMBER_SQL = "select id from (select row_number() over (order by id) as rownum,id from Find_list) as Find_list where rownum in (:rownumbers) order by id";
    @Override
    public List<Long> getIdsByRowNumbers(List<Long> rowNumbers) {
        Query query = entityManager.createNativeQuery(QUERY_IDS_BY_ROW_NUMBER_SQL);
        query.setParameter("rownumbers", rowNumbers);
        List<BigInteger> resultList = query.getResultList();
        return resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
    }

    @Override
    public List<Rawdata> findAll(Long start, Long end, Integer limit) {
        QRawdata qRawdata = QRawdata.rawdata;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (start != null && start > 0) {
            booleanBuilder.and(qRawdata.id.gt(start));
        }
        if (end != null && end > 0) {
            booleanBuilder.and(qRawdata.id.loe(end));
        }
        limit = limit == null ? 10 : limit;
        JPAQuery<?> query = new JPAQuery<Rawdata>(entityManager);
        return query.select(qRawdata)
                .from(qRawdata)
                .where(booleanBuilder)
                .limit(limit)
                .fetch();
    }

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
