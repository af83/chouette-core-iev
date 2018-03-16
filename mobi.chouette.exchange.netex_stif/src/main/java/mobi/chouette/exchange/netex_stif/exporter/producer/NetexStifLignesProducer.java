package mobi.chouette.exchange.netex_stif.exporter.producer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.netex_stif.NetexStifConstant;
import mobi.chouette.exchange.netex_stif.exporter.ExportableData;
import mobi.chouette.exchange.netex_stif.exporter.writer.NetexStifFileWriter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;

public class NetexStifLignesProducer {

	public void produce(Context context) throws Exception {
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		ExportableData collection = (ExportableData) context.get(Constant.EXPORTABLE_DATA);
		JobData jobData = (JobData) context.get(Constant.JOB_DATA);
		String rootDirectory = jobData.getPathName();

		Path dir = null;
		String fileName = (collection.getMappedLines().size() == 1 ? NetexStifConstant.LIGNE_FILE_NAME : NetexStifConstant.LIGNES_FILE_NAME);
        String fileNameForReporting = fileName;
		if (context.containsKey(NetexStifConstant.OUTPUT_SUB_PATH)) {
			String sub = (String) context.get(NetexStifConstant.OUTPUT_SUB_PATH);
			fileNameForReporting = sub+"/"+fileName;
			dir = Paths.get(rootDirectory, Constant.OUTPUT, sub);
		} else {
			dir = Paths.get(rootDirectory, Constant.OUTPUT);
		}
		File file = new File(dir.toFile(), fileName);

		NetexStifFileWriter writer = new NetexStifFileWriter();
		writer.writeLignesFile(collection, file);

		reporter.addFileReport(context, fileNameForReporting, IO_TYPE.OUTPUT);
		
	}

}
