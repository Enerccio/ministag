package cz.upol.inf.vanusanik.ministag.ui.guards;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;
import cz.upol.inf.vanusanik.ministag.ui.services.GarantController.ChosenCourse;
import cz.upol.inf.vanusanik.ministag.ui.services.GarantController.ChosenDepartment;

/**
 * Validates whether course name is valid (not empty and unique)
 * 
 * @author enerccio
 *
 */
@Named("GUARDCourseAbbr")
@ApplicationScoped
public class CourseAbbrValidator implements Validator {

	@Inject
	private ChosenDepartment ad;
	@Inject
	private ChosenCourse ac;
	@Inject
	private MinistagRepository repository;

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (ac.getCurrentCourse() != null && value.equals(ac.getCurrentCourse().getShortName()))
			return;
		if (!repository.uniqueCourseName(ad.getCurrentDepartment().getShortName(), (String) value))
			throw new ValidatorException(new FacesMessage("Zkratka musí být unikátní", ""));
	}

}
