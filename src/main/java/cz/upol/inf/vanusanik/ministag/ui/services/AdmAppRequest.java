package cz.upol.inf.vanusanik.ministag.ui.services;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.AppSettings;
import cz.upol.inf.vanusanik.ministag.model.entities.Roles;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;
import cz.upol.inf.vanusanik.ministag.ui.tools.Action;
import cz.upol.inf.vanusanik.ministag.ui.tools.Utils;

@Named("admApp")
@RequestScoped
public class AdmAppRequest {
	
	@Inject 
	private MinistagRepository repository;

	@Inject
	private Security security;
	
	@PostConstruct
	public void init() {
		AppSettings as = repository.getAppSettings();
		startYear = as.getStartOfYear().getTime();
		numWeeks = as.getNumWeeks();
	}
	
	private Date startYear;
	
	private int numWeeks;
	
	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public Date getStartYear() {
		return startYear;
	}

	public void setStartYear(Date startYear) {
		this.startYear = startYear;
	}

	public int getNumWeeks() {
		return numWeeks;
	}

	public void setNumWeeks(int numWeeks) {
		this.numWeeks = numWeeks;
	}

	public String save() {
		return security.runPrivileged(new Action() {
			
			@Override
			public String run() {
				AppSettings as = repository.getAppSettings();
				Calendar c = Calendar.getInstance();
				c.setTime(startYear);
				as.setNumWeeks(getNumWeeks());
				as.setStartOfYear(c);
				repository.save(as);
				return Utils.redirect("Nastavení aplikace ulženo", "/admApp.xhtml");
			}
		}, Roles.ADMIN);
	}
	
}
