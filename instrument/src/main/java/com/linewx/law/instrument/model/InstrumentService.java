package com.linewx.law.instrument.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class InstrumentService {
	
	@Autowired
	private InstrumentRepository instrumentRepository;

	@PostConstruct	
	protected void initialize() {
	}

	@Transactional
	public Instrument save(Instrument instrument) {
		instrumentRepository.save(instrument);
		return instrument;
	}

	@Transactional
	public void save(List<Instrument> instrumentList) {
		instrumentList.forEach(instrument -> instrumentRepository.save(instrument));
		//instrumentRepository.save(instrument);
		//return instrument;
	}


}
