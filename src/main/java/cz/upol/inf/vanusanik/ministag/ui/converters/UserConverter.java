package cz.upol.inf.vanusanik.ministag.ui.converters;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.User;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;

@ApplicationScoped
@Named("ConverterUser")
public class UserConverter implements Converter {
	
	@Inject private MinistagRepository repository;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return repository.find(value, User.class);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return ((User)value).getLogin();
	}

}
