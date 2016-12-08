package com.linewx.law.instrument.model.rawdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CustomRawdataRepository{
    List<Long> getIdsByRowNumbers(List<Long> rowNumbers);

    List<Rawdata> findAll(Long start, Long end, Integer limit);
}