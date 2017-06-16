package mobi.chouette.service;

import java.io.File;
import java.nio.file.Files;

import org.apache.log4j.BasicConfigurator;
import org.testng.annotations.Test;

import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.dummy.importer.DummyImporterInputValidator;

public class ParametersTest {

	@Test
	public void test() throws Exception {
		
		BasicConfigurator.configure();
		
		String filename = "src/test/resources/parameters.json";
		File f = new File(filename);
		byte[] bytes = Files.readAllBytes(f.toPath());
		String text = new String(bytes, "UTF-8");
		
		// log.info("ParametersTest.test() : \n" + payload.toString());
		InputValidator inputValidator =  new DummyImporterInputValidator();

		Parameters param = new Parameters(text, inputValidator);
		JSONUtil.toJSON(param.getConfiguration());
		JSONUtil.toJSON(param.getValidation());
		//log.info("ParametersTest.test() : \n" + result);
	}

}
