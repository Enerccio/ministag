package cz.upol.inf.vanusanik.ministag.ui.guards;

import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@Stateless
@FacesValidator("GUARDAboveZero")
public class AboveZeroValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		Number n = null;
		if (value instanceof String) {
			try {
				n = Integer.parseInt((String)value);
			} catch (Exception e) {
				try {
					n = Double.parseDouble((String)value);
				} catch (Exception ee) {
					
				}
			}
		} else if (value instanceof Number) {
			n = (Number)value;
		}
		
		if (n == null) {
			throw new ValidatorException(new FacesMessage("Nesprávný formát čísla!", "Nesprávný formát čísla!"));
		}
		
		if (n.doubleValue() <= 0) {
			throw new ValidatorException(new FacesMessage("Hodnota musí být větší než nula!", "Hodnota musí být větší než nula!"));
		}
	}

}
