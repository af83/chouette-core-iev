package mobi.chouette.exchange.netex_stif.validator;

import java.util.Calendar;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netex_stif.NetexStifConstant;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Footnote;

public class NoticeValidator extends AbstractValidator {

	public static final String LOCAL_CONTEXT = NetexStifConstant.NOTICE;

	protected String getLocalContext() {
		return LOCAL_CONTEXT;
	}

	@Override
	public void init(Context context) {
		super.init(context);
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		// -- preset checkpoints to OK if uncheck
		validationReporter.prepareCheckPointReport(context, NetexCheckPoints.L2_NeTExSTIF_Notice_1);
		validationReporter.prepareCheckPointReport(context, NetexCheckPoints.L2_NeTExSTIF_Notice_2);
	}

	public void addTypeOfNoticeRef(Context context, String objectId, String typeOfNoticeRef) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(NetexStifConstant.TYPE_OF_NOTICE_REF, typeOfNoticeRef);

	}

	/**
	 * @param context
	 * @param route
	 * @param lineNumber
	 * @param columnNumber
	 * @return
	 */
	public boolean validate(Context context, Footnote footnote, int lineNumber, int columnNumber) {
		checkChanged(context, NetexStifConstant.NOTICE, footnote, lineNumber, columnNumber);

		boolean result2 = checkModification(context, NetexStifConstant.NOTICE, footnote, lineNumber, columnNumber);
		boolean result3 = check2NeTExSTIFNotice2(context, footnote, lineNumber, columnNumber);
		if (result3)
			result3 = check2NeTExSTIFNotice1(context, footnote, lineNumber, columnNumber);
		
		return result2 && result3;

	}


	/**
	 * <b>Titre</b> :[Netex] Contrôle de l'objet Notice : présence de l'attribut Text
	 * <p>
	 * <b>R&eacute;ference Redmine</b> : <a target="_blank" href="https://projects.af83.io/issues/2301">Cartes #2301</a>
	 * <p>
	 * <b>Code</b> : 2-NeTExSTIF-Notice-1
	 * <p>
	 * <b>Variables</b> : néant
	 * <p>
	 * <b>Prérequis</b> : néant
	 * <p>
	 * <b>Prédicat</b> : L'attribut Text de l'objet Notice doit être renseigné
	 * <p>
	 * <b>Message</b> : {fichier}-Ligne {ligne}-Colonne {Colonne} : l'objet Notice d'identifiant {objectId} doit définir
	 * un texte
	 * <p>
	 * <b>Criticité</b> : error
	 * <p>
	 * 
	 *
	 * @param context
	 * @return
	 */
	public boolean check2NeTExSTIFNotice1(Context context, Footnote footnote, int lineNumber, int columnNumber) {
		// Implementation Controle 2-NeTExSTIF-Notice-1 : [Netex] Contrôle de l'objet Notice
		// : présence de l'attribut Text
		boolean result = footnote.getLabel() != null && !footnote.getLabel().isEmpty();

		if (!result) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			String fileName = (String) context.get(Constant.FILE_NAME);
			DataLocation location = new DataLocation(fileName, lineNumber, columnNumber);
			location.setObjectId(footnote.getObjectId());
			validationReporter.addCheckPointReportError(context, null, NetexCheckPoints.L2_NeTExSTIF_Notice_1,NetexCheckPoints.L2_NeTExSTIF_Notice_1, location);
		}
		return result;
	}

	/**
	 * <b>Titre</b> :[Netex] Contrôle de l'objet Notice : TypeOfNoticeRef
	 * <p>
	 * <b>R&eacute;ference Redmine</b> : <a target="_blank" href="https://projects.af83.io/issues/2302">Cartes #2302</a>
	 * <p>
	 * <b>Code</b> : 2-NeTExSTIF-Notice-2
	 * <p>
	 * <b>Variables</b> : néant
	 * <p>
	 * <b>Prérequis</b> : néant
	 * <p>
	 * <b>Prédicat</b> : Seules les Notices de type ServiceJourneyNotice sont importées
	 * <p>
	 * <b>Message</b> : {fichier}-Ligne {ligne}-Colonne {Colonne} : l'objet Notice d'identifiant {objectId} de type
	 * {TypeOfNoticeRef} est ignoré
	 * <p>
	 * <b>Criticité</b> : warning
	 * <p>
	 * 
	 *
	 * @param context
	 * @return
	 */
	public boolean check2NeTExSTIFNotice2(Context context, Footnote footnote, int lineNumber, int columnNumber) {
		// Implementation Controle 2-NeTExSTIF-Notice-2 : [Netex] Contrôle de l'objet Notice
		// :
		// TypeOfNoticeRef
		boolean result = true;

		Context footnoteContext = getObjectContext(context, LOCAL_CONTEXT, footnote.getObjectId());
		// récupération de la valeur d'attribut sauvegardée
		String type = (String) footnoteContext.get(NetexStifConstant.TYPE_OF_NOTICE_REF);

		if (type == null || !type.equals(NetexStifConstant.SERVICE_JOURNEY_NOTICE)) {
			result = false;
		}

		if (!result) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			String fileName = (String) context.get(Constant.FILE_NAME);
			DataLocation location = new DataLocation(fileName, lineNumber, columnNumber);
			location.setObjectId(footnote.getObjectId());
			validationReporter.addCheckPointReportError(context, null, NetexCheckPoints.L2_NeTExSTIF_Notice_2, NetexCheckPoints.L2_NeTExSTIF_Notice_2,location, "" + type);
		}

		return result;
	}

	public boolean checkChanged(Context context, String type, Footnote object, int lineNumber, int columnNumber) {

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);

		boolean result = object.getCreationTime().before(c.getTime());

		if (!result) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
			String fileName = (String) context.get(Constant.FILE_NAME);
			DataLocation location = new DataLocation(fileName, lineNumber, columnNumber, object.getObjectId());
			validationReporter.addCheckPointReportError(context, null, NetexCheckPoints.L2_NeTExSTIF_5,NetexCheckPoints.L2_NeTExSTIF_5, location, type);
			// reset creation time to now
			object.setCreationTime(Calendar.getInstance().getTime());
		}
		return result;
	}

	public boolean checkModification(Context context, String type, Footnote object, int lineNumber, int columnNumber) {

		boolean result = true;

		Context objectContext = getObjectContext(context, getLocalContext(), object.getObjectId());
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		if (objectContext.containsKey(NetexStifConstant.MODIFICATION)) {
			validationReporter.prepareCheckPointReport(context, NetexCheckPoints.L2_NeTExSTIF_6);
			String modification = (String) objectContext.get(NetexStifConstant.MODIFICATION);
			result = !modification.equals("delete");
		}
		if (!result) {
			String fileName = (String) context.get(Constant.FILE_NAME);
			DataLocation location = new DataLocation(fileName, lineNumber, columnNumber, object.getObjectId());
			validationReporter.addCheckPointReportError(context, null, NetexCheckPoints.L2_NeTExSTIF_6,NetexCheckPoints.L2_NeTExSTIF_6, location, type);
		}
		return result;

	}

}
