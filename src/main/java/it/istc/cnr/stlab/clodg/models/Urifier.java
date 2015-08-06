package it.istc.cnr.stlab.clodg.models;

import it.istc.cnr.stlab.clodg.Homonym;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ibm.icu.text.Transliterator;

public class Urifier {

	public static String toURI(String label) {
		// remove accents
		label = StringUtils.stripAccents(label);
		// lowercase:
		label = label.toLowerCase();
		// remove various characters:
		// '
		label = label.replaceAll("[\\']", "");
		// replace various characters with whitespace:
		// - + ( ) . , & " / ??? !
		label = label.replaceAll("[;.,&\\\"\\/???!]", "");
		// squeeze whitespace to dashes:
		label = label.replaceAll(" ", "-");

		label = label.replaceAll("\\-$", "");

		try {
			return URLEncoder.encode(label, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return label;
		}
	}

	public static String toURI(String namespace, String label, String email,
			Map<String, Map<String, Homonym>> homonymsMap) {
		label = toURI(label);

		if (homonymsMap != null) {
			Map<String, Homonym> homonyms = homonymsMap.get(label);
			if (homonyms != null) {
				Homonym homonym = homonyms.get(email);
				if (homonym != null)
					return namespace + homonym.getId();
			}
		}

		return namespace + label;
	}

	public static void main(String[] args) {
		String s = "S??ren Auer";
		System.out.println(StringUtils.stripAccents(s));
	}
}
