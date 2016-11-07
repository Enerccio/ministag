package cz.upol.inf.vanusanik.ministag.ui.guards;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.ui.services.Security.AddEditUserRequest.CurrentlyEditedUser;

@Named("GUARDPasswordCriteria")
@ApplicationScoped
public class PasswordCriteriaValidator implements Validator {

	private @Inject CurrentlyEditedUser ceu;
	
	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object value) throws ValidatorException {
		if (value == null) {
			throw new ValidatorException(new FacesMessage("Nesprávne heslo", "Heslo musí mít alespoň 5 znaků a obsahovat číslici"));
		}
		String password = value.toString();
		if (password.length() == 0 && ceu.getCurrentUser() != null)
			return;
		if (password.length() < 5) {
			throw new ValidatorException(new FacesMessage("Nesprávne heslo", "Heslo musí mít alespoň 5 znaků a obsahovat číslici"));
		}
		if (!password.matches(".*\\d+.*")) {
			throw new ValidatorException(new FacesMessage("Nesprávne heslo", "Heslo musí mít alespoň 5 znaků a obsahovat číslici"));
		}
	}

}
