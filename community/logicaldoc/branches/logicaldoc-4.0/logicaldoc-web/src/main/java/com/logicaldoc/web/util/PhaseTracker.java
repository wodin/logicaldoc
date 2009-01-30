package com.logicaldoc.web.util;

import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * Lets you check JSF phases (for debugging purpose only). To activate, add:
 * <lifecycle> <phase-listener>com.logicaldoc.web.util.PhaseTracker</phase-listener>
 * </lifecycle> into faces-config.xml
 * 
 * @version $Revision: 1.4 $ $Date: 2007/01/23 17:07:25 $
 * @author jesusapl
 * 
 */
public class PhaseTracker implements PhaseListener {

	/**
	 */
	private static final long serialVersionUID = -2154879637481389236L;

	private static final String PHASE_PARAMETER = "com.logicaldoc.web.util.phaseTracker.phase";

	private static final Logger logger = Logger.getLogger("com.logicaldoc.web.util");

	private static String phase = null;

	public void setPhase(String newValue) {
		phase = newValue;
	}

	public PhaseId getPhaseId() {

		if (phase == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			phase = (String) context.getExternalContext().getInitParameter(PHASE_PARAMETER);
		}

		PhaseId phaseId = PhaseId.ANY_PHASE;

		if (phase != null) {

			if ("RESTORE_VIEW".equals(phase))
				phaseId = PhaseId.RESTORE_VIEW;
			else if ("APPLY_REQUEST_VALUES".equals(phase))
				phaseId = PhaseId.APPLY_REQUEST_VALUES;
			else if ("PROCESS_VALIDATIONS".equals(phase))
				phaseId = PhaseId.PROCESS_VALIDATIONS;
			else if ("UPDATE_MODEL_VALUES".equals(phase))
				phaseId = PhaseId.UPDATE_MODEL_VALUES;
			else if ("INVOKE_APPLICATION".equals(phase))
				phaseId = PhaseId.INVOKE_APPLICATION;
			else if ("RENDER_RESPONSE".equals(phase))
				phaseId = PhaseId.RENDER_RESPONSE;
			else if ("ANY_PHASE".equals(phase))
				phaseId = PhaseId.ANY_PHASE;
		}

		return phaseId;
	}

	public void beforePhase(PhaseEvent e) {
		logger.info("BEFORE " + e.getPhaseId());
	}

	public void afterPhase(PhaseEvent e) {
		logger.info("AFTER " + e.getPhaseId());
	}
}
