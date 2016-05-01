package it.istc.cnr.stlab.clodg.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import net.coobird.thumbnailator.Thumbnails;

public class ResizeImg {

	BufferedImage createResizedCopy(Image originalImage, int scaledWidth,
			int scaledHeight, boolean preserveAlpha) {
		System.out.println("resizing...");
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight,
				imageType);
		Graphics2D g = scaledBI.createGraphics();
		if (preserveAlpha) {
			g.setComposite(AlphaComposite.Src);
		}
		g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
		g.dispose();
		return scaledBI;
	}

	public void addResizeImages(Model model, Property property,
			Model imagesModel) {
		model.removeAll(null, property, (RDFNode) null);
		model.add(imagesModel);
	}

	public void resize(File folder, File outFolder) {
		if (!outFolder.exists()) {
			outFolder.mkdirs();
		}

		for (File file : folder.listFiles()) {
			List<File> files = new ArrayList<File>();
			files.add(file);
			try {
				Thumbnails
						.fromFiles(files)
						.size(400, 400)
						.toFile(outFolder.getPath() + File.separator
								+ file.getName());
			} catch (Exception e) {
				System.err.println(file.getName());
			}
		}
	}

	public Model resize(Model imagesModel, File outFolder) {

		Model model = ModelFactory.createDefaultModel();

		if (!outFolder.exists()) {
			outFolder.mkdirs();
		}

		StmtIterator stmtIterator = imagesModel.listStatements();
		while (stmtIterator.hasNext()) {

			Statement stmt = stmtIterator.next();
			Resource person = stmt.getSubject();
			Property predicate = stmt.getPredicate();
			RDFNode depiction = stmt.getObject();

			int lastSlash = person.getURI().lastIndexOf("/");
			int lastHash = person.getURI().lastIndexOf("#");

			String ns = null;
			if (lastSlash > lastHash) {
				ns = person
						.getURI()
						.substring(0, lastSlash)
						.replace(
								"http://data.semanticweb.org/conference/eswc/2014/paper/",
								"");
			} else {
				ns = person
						.getURI()
						.substring(0, lastHash)
						.replace(
								"http://data.semanticweb.org/conference/eswc/2014/paper/",
								"");
				;
			}
			// ns = ns.substring(0, ns.length()-2);

			String outfolder = outFolder.getPath() + File.separator + ns;

			System.out.println(ns);
			File f = new File(outfolder);

			if (!f.exists())
				f.mkdirs();

			String url = depiction.toString().trim();

			if (url.equals("http://luca.costabello.info/images/paper81-thumb.jpg")) {
				url = "http://localhost/eswc-stc/img/papers/paper81-thumb.jpg";
			}
			if (url.equals("http://wit.istc.cnr.it/eswc-stc/images/papers/poster/11.tiff")) {
				url = "http://localhost/eswc-stc/img/papers/11.png";
			}
			if (url.endsWith("/") || url.endsWith("#"))
				url = url.substring(0, url.length() - 2);

			System.out.println(url);

			String depictionURL = url;

			lastSlash = depictionURL.lastIndexOf("/");
			lastHash = depictionURL.lastIndexOf("#");

			String fileName = null;
			if (lastSlash > lastHash) {
				fileName = depictionURL.substring(lastSlash + 1);
			} else {
				fileName = depictionURL.substring(lastHash + 1);
			}

			File file = new File(outfolder + File.separator + fileName);

			if (file.exists()) {
				long time = System.currentTimeMillis();

				String extension = FilenameUtils.getExtension(fileName);

				if (!extension.isEmpty())
					fileName = fileName.substring(0,
							(fileName.length() - extension.length()) - 1)
							+ "_"
							+ time + ".png";
				else
					fileName = fileName + "_" + time + ".png";
				file = new File(outFolder.getPath() + File.separator + fileName);

			}

			String extension = FilenameUtils.getExtension(fileName);
			try {
				List<URL> urls = new ArrayList<URL>();
				urls.add(new URL(depictionURL));
				Thumbnails.fromURLs(urls).size(400, 400).toFile(file);

			} catch (Exception e) {
				System.err.println("With file " + depictionURL
						+ " with extension " + extension);
				e.printStackTrace();
			}

			model.add(
					person,
					predicate,
					model.createResource("http://wit.istc.cnr.it/eswc-stc/images/papers/resized/"
							+ ns + "/" + fileName));

		}

		return model;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// new ResizeImg().resize(new File("out/img_tmp"), new
		// File("out/img_tmp_resized"));

		/*
		 * String sparql = "PREFIX dbpedia: <http://dbpedia.org/ontology/> " +
		 * "CONSTRUCT {?paper dbpedia:thumbnail ?thumb} " +
		 * "WHERE{?paper dbpedia:thumbnail ?thumb}";
		 * 
		 * Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		 * QueryExecution execution = QueryExecutionFactory.create(query,
		 * FileManager.get().loadModel("out/eswc_data_final_swdf.rdf"));
		 * 
		 * Model thumbModel = execution.execConstruct();
		 * 
		 * Model model = new ResizeImg().resize(thumbModel, new
		 * File("out/img_papers_resized"));
		 * 
		 * //Model model = new
		 * ResizeImg().resize(FileManager.get().loadModel("out/depictions_pmc.rdf"
		 * ), new File("out/img_pmc")); OutputStream out; try { out = new
		 * FileOutputStream(new File("out/depictions_papers_resized.rdf"));
		 * model.write(out); } catch (FileNotFoundException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		// TODO this is the bit to actually resize all images

		File imgIn = new File("./images");
		File imgOut = new File("./imgresized");

		imgOut.mkdirs();

		for (File i : imgIn.listFiles()) {
			resize(i, imgOut.getAbsolutePath());

		}

	}

	private static void resize(File fileIn, String outFolder) {

		if (fileIn.isDirectory()) {
			outFolder = outFolder + File.separator + fileIn.getName();
			new File(outFolder).mkdirs();
			for (File i : fileIn.listFiles()) {
				resize(i, outFolder);
			}
		} else {
			System.out.println("converting " + fileIn);
			try {
				Thumbnails.of(fileIn.getAbsolutePath()).size(400, 400)
						.outputFormat("jpg")
						.toFile(outFolder + File.separator + fileIn.getName());
			} catch (IOException e) {
				System.err.println("ERROR CONVERTING "
						+ fileIn.getAbsolutePath());

				e.printStackTrace();
			} catch (Exception e) {
				System.err.println("ERROR CONVERTING "
						+ fileIn.getAbsolutePath());

				e.printStackTrace();
			}
		}
	}

}