package cz.upol.inf.vanusanik.ministag.ui.guards;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.ui.services.SecurityController.AddEditUserRequest.CurrentlyEditedUser;

/**
 * Validates if two passwords entered are identical
 * @author enerccio
 *
 */
@Named("GAURDPasswordEquals")
@ApplicationScoped
public class PasswordEqualsValidator implements Validator {

	private @Inject CurrentlyEditedUser ceu;

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		UIInput passwdComponent = (UIInput) component.getAttributes().get("passwordComponentBinding");
		String passwd = (String) passwdComponent.getValue();
		if (passwd == null)
			return; // bad password so don't check this

		if (passwd.length() == 0 && ceu.getCurrentUser() != null)
			return;

		if (!value.equals(passwd)) {
			FacesMessage msg = new FacesMessage("Hesla se neshodují", "");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}

}
