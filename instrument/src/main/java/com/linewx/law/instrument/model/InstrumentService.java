package com.linewx.law.instrument.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class InstrumentService {
	
	@Autowired
	private InstrumentRepository instrumentRepository;

	@PostConstruct	
	protected void initialize() {
		save(new Instrument("user"));
		save(new Instrument("admin"));
	}

	@Transactional
	public Instrument save(Instrument instrument) {
		instrumentRepository.save(instrument);
		return instrument;
	}

}