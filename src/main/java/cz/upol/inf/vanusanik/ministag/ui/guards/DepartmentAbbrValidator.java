package cz.upol.inf.vanusanik.ministag.ui.guards;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.Department;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;
import cz.upol.inf.vanusanik.ministag.ui.services.DepartmentController.ActiveDepartment;

/**
 * Validates whether department abbreviation is valid (not empty and unique)
 * @author enerccio
 *
 */
@Named("GUARDDeptAbbr")
@ApplicationScoped
public class DepartmentAbbrValidator implements Validator {

	@Inject
	private ActiveDepartment ad;
	@Inject
	private MinistagRepository repository;

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value.equals(ad.getActiveDepartment()))
			return;
		if (repository.find(value, Department.class) != null)
			throw new ValidatorException(new FacesMessage("Zkratka musí být unikátní", ""));
	}

}
