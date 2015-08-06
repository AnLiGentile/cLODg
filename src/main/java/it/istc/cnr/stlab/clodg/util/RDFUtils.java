package it.istc.cnr.stlab.clodg.util;

import com.hp.hpl.jena.rdf.model.Resource;

public class RDFUtils {

	public static String getLocalName(Resource resource) {
		if (resource.isURIResource()) {
			String uri = resource.getURI();
			int lastSlash = uri.lastIndexOf("/");
			int lastHash = uri.lastIndexOf("#");

			int index;
			if (lastSlash > lastHash)
				index = lastSlash + 1;
			else
				index = lastHash + 1;

			return uri.substring(index);
		} else
			return resource.toString();
	}

	public static String getNameSpace(Resource resource) {
		if (resource.isURIResource()) {
			String uri = resource.getURI();
			int lastSlash = uri.lastIndexOf("/");
			int lastHash = uri.lastIndexOf("#");

			int index;
			if (lastSlash > lastHash)
				index = lastSlash;
			else
				index = lastHash;

			return uri.substring(0, index);
		} else
			return resource.toString();
	}

}
