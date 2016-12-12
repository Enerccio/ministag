package cz.upol.inf.vanusanik.ministag.ui.guards;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Checks that value is not empty
 * 
 * @author enerccio
 *
 */
@FacesValidator("GUARDNotEmpty")
public class NotEmptyValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value.toString().equals(""))
			throw new ValidatorException(new FacesMessage("Hodnota musí být vyplněná", ""));
	}

}
