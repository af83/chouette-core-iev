package mobi.chouette.exchange.neptune.validator;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.neptune.importer.NeptuneImporterInputValidator;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class NeptuneValidatorInputValidator extends NeptuneImporterInputValidator {

	@Override
	public boolean check(AbstractParameter abstractParameter, ValidationParameters validationParameters, String fileName) {
		if (!(abstractParameter instanceof NeptuneValidateParameters)) {
			log.error("invalid parameters for validator " + abstractParameter.getClass().getName());
			return false;
		}
		if (validationParameters == null) {
			log.error("no validation parameters for validation ");
			return false;
		}

		return super.check(abstractParameter, validationParameters, fileName);
	}

	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new NeptuneValidatorInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(NeptuneValidatorInputValidator.class.getName(), new DefaultFactory());
	}

}