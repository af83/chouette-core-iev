package mobi.chouette.exchange.netex_stif.importer;

import java.nio.file.Paths;

import org.testng.Assert;

import mobi.chouette.exchange.parameters.AbstractExportParameter;

public class NetexStifImporterInputValidatorTests {
	//@Test(groups = { "InputValidator" }, description = "test good inputs")
	public void verifyGoodInputs() throws Exception
	{
		NetexStifImporterInputValidator validator = new NetexStifImporterInputValidator();
		NetexStifImportParameters parameters = new NetexStifImportParameters();
	
		boolean result = validator.checkParameters(parameters);

		Assert.assertTrue(result, "check for good parameters");
		 result = validator.checkFilename("data.zip");

		Assert.assertTrue(result, "check for good  file name");
		 result = validator.checkFilename("data.txt");

		Assert.assertTrue(result, "check for good file name ");
		
		result = validator.checkFile("good.xml", Paths.get("src/test/data/good.xml"), null);
		Assert.assertTrue(result, "check for good txt file");
		
		
		result = validator.checkFile("good.zip", Paths.get("src/test/data/good.zip"), null);
		Assert.assertTrue(result, "check for good zip file");
				
	}

	//@Test(groups = { "InputValidator" }, description = "test bad inputs")
	public void verifyBadInputs() throws Exception
	{
		NetexStifImporterInputValidator validator = new NetexStifImporterInputValidator();

		boolean result = validator.checkParameters(new BadParameters());
		Assert.assertFalse(result, "check for parameter class");
		
		NetexStifImportParameters parameters = new NetexStifImportParameters();
		
		result = validator.checkParameters(parameters);
		Assert.assertFalse(result, "check for wrong type");

		
		result = validator.checkParameters(parameters);
		Assert.assertFalse(result, "check for no object_id_prefix");

		
		result = validator.checkParameters(parameters);
		Assert.assertFalse(result, "check for empty object_id_prefix");

		
		result = validator.checkFilename(null);
		Assert.assertFalse(result, "check for filename");
		
		
		result = validator.checkFile("bad.zip", Paths.get("src/test/data/bad.zip"), null);
		Assert.assertFalse(result, "check for bad zip file");
		
	}


	private class BadParameters extends AbstractExportParameter
	{

	}
}
