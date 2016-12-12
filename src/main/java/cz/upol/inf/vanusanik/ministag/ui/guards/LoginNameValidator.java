package cz.upol.inf.vanusanik.ministag.ui.guards;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.User;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;

/**
 * Checks whether login name is valid (unique and not less than 5 characters)
 * 
 * @author enerccio
 *
 */
@Named("GUARDLoginName")
@ApplicationScoped
public class LoginNameValidator implements Validator {

	@Inject
	private MinistagRepository repository;

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object value) throws ValidatorException {
		String login = value.toString();
		if (login.length() < 5) {
			throw new ValidatorException(new FacesMessage("Nesprávný login", "Login musí mít nejméně 5 znaků"));
		}
		if (repository.find(login, User.class) != null) {
			throw new ValidatorException(new FacesMessage("Nesprávný login", "Login již existuje"));
		}
	}

}
