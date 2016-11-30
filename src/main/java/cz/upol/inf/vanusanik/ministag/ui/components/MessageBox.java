package cz.upol.inf.vanusanik.ministag.ui.components;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

/**
 * MessageBox ajax component
 * @author enerccio
 *
 */
@FacesComponent("messageBox")
public class MessageBox extends UIComponentBase implements NamingContainer {

	/**
	 * As required by cc:interface.
	 */
	@Override
	public String getFamily() {
		return UINamingContainer.COMPONENT_FAMILY;
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);
	}
}
