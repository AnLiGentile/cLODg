package it.istc.cnr.stlab.clodg.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;

public class PeopleToCheck {

	public static Set<String> people;

	static {
		people = new HashSet<String>();
		people.add("http://data.semanticweb.org/person/cathal-gurrin");
		people.add("http://data.semanticweb.org/person/luciano-floridi");
		people.add("http://data.semanticweb.org/person/huakang-li");
		people.add("http://data.semanticweb.org/person/marcos-da-silveira");
		people.add("http://data.semanticweb.org/person/duy-dinh");
		people.add("http://data.semanticweb.org/person/craig-weber");
		people.add("http://data.semanticweb.org/person/yi-liu");
		people.add("http://data.semanticweb.org/person/nuria-casellas");
		people.add("http://data.semanticweb.org/person/johannes-heinecke");
		people.add("http://data.semanticweb.org/person/torsten-leidig");
		people.add("http://data.semanticweb.org/person/jesus-arias-fisteus");
		people.add("http://data.semanticweb.org/person/shen-gao");
		people.add("http://data.semanticweb.org/person/yannis-marketakis");
		people.add("http://data.semanticweb.org/person/yannis-stavrakas");
		people.add("http://data.semanticweb.org/person/xin-wang");
		people.add("http://data.semanticweb.org/person/ioannis-n-athanasiadis");
		people.add("http://data.semanticweb.org/person/stuart-n-wrigley");
		people.add("http://data.semanticweb.org/person/joao-magalhaes");
		people.add("http://data.semanticweb.org/person/robert-tucker");
		people.add("http://data.semanticweb.org/person/serge-tymaniuk");
		people.add("http://data.semanticweb.org/person/marcin-sydow");
		people.add("http://data.semanticweb.org/person/david-carral");
		people.add("http://data.semanticweb.org/person/peter-schauss");
		people.add("http://data.semanticweb.org/person/nikos-minadakis");
		people.add("http://data.semanticweb.org/person/michalis-mountantonakis");
		people.add("http://data.semanticweb.org/person/clemens-martin");
		people.add("http://data.semanticweb.org/person/edith-leitner");
		people.add("http://data.semanticweb.org/person/damaris-fuentes-lorenzo");
		people.add("http://data.semanticweb.org/person/mikolaj-morzy");
		people.add("http://data.semanticweb.org/person/julio-cesar-dos-reis");
		people.add("http://data.semanticweb.org/person/simon-fischer");
		people.add("http://data.semanticweb.org/person/fabio-gagliardi-cozman");
		people.add("http://data.semanticweb.org/person/mercedes-martinez-gonzalez");
		people.add("http://data.semanticweb.org/person/ruediger-grimm");
		people.add("http://data.semanticweb.org/person/john-zeleznikow");
		people.add("http://data.semanticweb.org/person/dan-scott");
		people.add("http://data.semanticweb.org/person/saisai-gong");
		people.add("http://data.semanticweb.org/person/martina-hartl");
		people.add("http://data.semanticweb.org/person/kees-van-deemter");
		people.add("http://data.semanticweb.org/person/dominik-wienand");
		people.add("http://data.semanticweb.org/person/christoph-schlieder");
		people.add("http://data.semanticweb.org/person/david-celestin-faye");
		people.add("http://data.semanticweb.org/person/giuseppe-scavo");
		people.add("http://data.semanticweb.org/person/alexander-boer");
		people.add("http://data.semanticweb.org/person/matteo-casu");
		people.add("http://data.semanticweb.org/person/rigo-wenning");
		people.add("http://data.semanticweb.org/person/erich-schweighofer");
		people.add("http://data.semanticweb.org/person/mohamed-sherif");
		people.add("http://data.semanticweb.org/person/jorge-gonzalez-conejero");
		people.add("http://data.semanticweb.org/person/hugh-c-davis");
		people.add("http://data.semanticweb.org/person/john-p-mccrae");
		people.add("http://data.semanticweb.org/person/thanos-yannakis");
		people.add("http://data.semanticweb.org/person/pavan-kapanipathi");
		people.add("http://data.semanticweb.org/person/danyun-xu");
		people.add("http://data.semanticweb.org/person/leon-derczynski");
		people.add("http://data.semanticweb.org/person/antonio-maccioni");
		people.add("http://data.semanticweb.org/person/sergios-soursos");
		people.add("http://data.semanticweb.org/person/tobias-weller");
		people.add("http://data.semanticweb.org/person/dominique-revuz");
		people.add("http://data.semanticweb.org/person/alessio-bosca");
		people.add("http://data.semanticweb.org/person/maribel-acosta-deibe");
		people.add("http://data.semanticweb.org/person/tom-van-engers");
		people.add("http://data.semanticweb.org/person/ugo-pagallo");
		people.add("http://data.semanticweb.org/person/ioanna-zidianaki");
		people.add("http://data.semanticweb.org/person/dirk-hovy");
		people.add("http://data.semanticweb.org/person/alberto-fernandez-gil");
		people.add("http://data.semanticweb.org/person/christian-gottron");
		people.add("http://data.semanticweb.org/person/johann-schaible");
		people.add("http://data.semanticweb.org/person/mingyi-guo");
		people.add("http://data.semanticweb.org/person/bene-rodriguez-castro");
		people.add("http://data.semanticweb.org/person/chitra-venkataramani");
		people.add("http://data.semanticweb.org/person/daniel-vila-suero");
		people.add("http://data.semanticweb.org/person/david-duce");
		people.add("http://data.semanticweb.org/person/pramod-jamkhedkar");
		people.add("http://data.semanticweb.org/person/guillaume-blin");
		people.add("http://data.semanticweb.org/person/artemis-parvizi");
		people.add("http://data.semanticweb.org/person/michel-leclere");
		people.add("http://data.semanticweb.org/person/bei-xu");
		people.add("http://data.semanticweb.org/person/marta-poblet");
		people.add("http://data.semanticweb.org/person/jorge-arnulfo-quiane-ruiz");
		people.add("http://data.semanticweb.org/person/catherine-roussey");
		people.add("http://data.semanticweb.org/person/kenneth-forbus");

	}

	public static void main(String[] args) {

		for (String s : people) {
			int count = StringUtils.countMatches(s, "-");

			if (count > 1) {
				System.out.println(s);
			}
		}

	}
}
