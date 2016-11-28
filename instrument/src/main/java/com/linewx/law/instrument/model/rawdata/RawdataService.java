package com.linewx.law.instrument.model.rawdata;

import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.instrument.model.InstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RawdataService {
	
	@Autowired
	private RawdataRepository rawdataRepository;

	public Long count() {
		return rawdataRepository.count();
	}


	public List<Rawdata> getData(int page) {
		Page<Rawdata> rawdatas = rawdataRepository.findAll(new PageRequest(page, 100));
		return rawdatas.getContent();
	}

}
