package org.validator.XMLEngine;

import java.io.File;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.validator.utils.FileUtils;
import org.validator.utils.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.XMLReader;

public class XMLProcessorEngine {

	private final Vector<String> events = new Vector<String> ();

	public XMLProcessorEngine(String workDir, File metadata, String subjectArea) {

		//list of objects to export
		events.add("PhysicalColumn");
		events.add("LogicalKey");
		events.add("LogicalTableSource");
		events.add("PhysicalTable");
		events.add("PresentationTable");
		events.add("LogicalColumn");
		events.add("Schema");
		events.add("PhysicalCatalog");
		events.add("Database");
		events.add("PresentationColumn");
		events.add("PresentationCatalog");
		events.add("LogicalTable");
		events.add("BusinessModel");
		events.add("MeasureDefn");
		events.add("PhysicalKey");

		long start = System.currentTimeMillis();
		ExecutorService threadPool = Executors.newFixedThreadPool(3);

		for (String tagName : events)
			threadPool.submit(new Processor(tagName, metadata, workDir));

		threadPool.shutdown();

		try {
			threadPool.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long middle = System.currentTimeMillis();

		XMLReader		XMLr = FileUtils.getXMLReader();
		SaxToDom		xml = new SaxToDom(null, XMLr, workDir);
		Vector<String>	vFindSA = new Vector<String> ();
		vFindSA.add(subjectArea);

		Document doc = xml.makeDom("PresentationCatalog", vFindSA);
		XMLUtils.saveDocument2File(doc, workDir + "metadata.xml");

		long end = System.currentTimeMillis();
		System.out.println((middle - start) / 1000);
		System.out.println((end - middle) / 1000);

		System.out.println((end - start) / 1000);
	}
}
