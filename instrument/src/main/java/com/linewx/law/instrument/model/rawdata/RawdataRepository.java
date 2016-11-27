package com.linewx.law.instrument.model.rawdata;

import com.linewx.law.instrument.model.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RawdataRepository extends JpaRepository<Rawdata, Long> {

}