package org.eswc.conferences.data.util;

import java.util.HashMap;
import java.util.Map;

import org.eswc.conferences.data.form.Person;

//TODO not a very good approach, but we need data quickly for now
// this must be changed and be dinamic in the future
public class KnownPerson {

	public static Map<String, Person> knownPersons;
	static {
		// **************** poster and demo
		knownPersons = new HashMap<String, Person>();
		knownPersons.put("Simone Paolo Ponzetto", new Person("Simone Paolo",
				"Ponzetto"));
		knownPersons.put("Andrea Giovanni Nuzzolese", new Person(
				"Andrea Giovanni", "Nuzzolese"));
		knownPersons.put("Jesus Arias Fisteus", new Person("Jesus",
				"Arias Fisteus"));
		knownPersons.put("Norberto Fernandez Garcia", new Person("Jesus",
				"Arias Fisteus"));
		knownPersons.put("Luis Sanchez Fernandez", new Person("Luis",
				"Sanchez Fernandez"));
		knownPersons.put("Radityo Eko Prasojo", new Person("Radityo",
				"Eko Prasojo"));
		knownPersons.put("Jeff Z. Pan", new Person("Jeff Z.", "Pan"));
		knownPersons.put("Joao Rocha Da Silva", new Person("Joao",
				"Rocha Da Silva"));
		knownPersons.put("Joao Correia Lopes", new Person("Joao",
				"Correia Lopes"));
		knownPersons.put("Alexander Arturo Mera Caraballo", new Person(
				"Alexander Arturo", "Mera Caraballo"));
		knownPersons.put("Narciso Moura Arruda Junior", new Person("Narciso",
				"Moura Arruda Junior"));
		knownPersons.put("Bernardo Pereira Nunes", new Person("Bernardo",
				"Pereira Nunes"));
		knownPersons.put("Giseli Rabello Lopes", new Person("Giseli",
				"Rabello Lopes"));
		knownPersons.put("Marco Antonio Casanova", new Person("Marco Antonio",
				"Casanova"));
		knownPersons.put("Marco A. Casanova", new Person("Marco Antonio",
				"Casanova"));
		knownPersons.put("Axel-Cyrille Ngonga Ngomo", new Person(
				"Axel-Cyrille", "Ngonga Ngomo"));
		knownPersons.put("Pedro H. R. Assis", new Person("Pedro", "Assis"));
		knownPersons.put("Tom De Nies", new Person("Tom", "De Nies"));
		knownPersons.put("Laurens De Vocht", new Person("Laurens", "De Vocht"));
		knownPersons.put("Rik Van de Walle", new Person("Rik", "Van de Walle"));
		knownPersons.put("Jose Luis Redondo-Garcia", new Person("Jose Luis",
				"Redondo-Garcia"));
		knownPersons.put("Lilia Perez Romero", new Person("Lilia",
				"Perez Romero"));
		knownPersons.put("Daniela Espinoza Molina", new Person("Daniela",
				"Espinoza Molina"));
		knownPersons.put("Octavian Corneliu Dimitru", new Person("Octavian",
				"Corneliu Dimitru"));
		knownPersons.put("Octavian Corneliu Dimitru", new Person("Octavian",
				"Corneliu Dimitru"));
		knownPersons.put("Raul Garcia Castro", new Person("Raul",
				"Garcia Castro"));
		knownPersons.put("Miguel Esteban Gutierrez", new Person(
				"Miguel Esteban", "Gutierrez"));
		knownPersons.put("Angel Luis Garrido", new Person("Angel Luis",
				"Garrido"));
		knownPersons.put("Jose Eduardo Talavera Herrera", new Person(
				"Jose Eduardo", "Talavera Herrera"));
		knownPersons.put("Giseli Rabello Lopes", new Person("Giseli",
				"Rabello Lopes"));
		knownPersons.put("Maria Esther-Vidal", new Person("Maria Esther",
				"Vidal"));
		knownPersons.put("Maria Esther Vidal", new Person("Maria Esther",
				"Vidal"));
		knownPersons.put("Corneliu Valentin Stanciu", new Person(
				"Corneliu Valentin", "Stanciu"));
		// ****************end poster and demo

		// ****************mashup
		knownPersons.put("Miel Vander Sande",
				new Person("Miel", "Vander Sande"));
		knownPersons.put("Dat Trinh Tuan", new Person("Dat", "Trinh Tuan"));
		knownPersons.put("Craig A. Knoblock", new Person("Craig A.",
				"Craig A. Knoblock"));
		knownPersons.put("Yael Ben Dov", new Person("Yael", "Ben Dov"));
		knownPersons.put("Simone Paolo Ponzetto", new Person("Simone Paolo",
				"Ponzetto"));
		knownPersons.put("Andrea Giovanni Nuzzolese", new Person(
				"Andrea Giovanni", "Nuzzolese"));
		knownPersons.put("Jesus Arias Fisteus", new Person("Jesus",
				"Arias Fisteus"));
		knownPersons.put("Norberto Fernandez Garcia", new Person("Jesus",
				"Arias Fisteus"));
		knownPersons.put("Luis Sanchez Fernandez", new Person("Luis",
				"Sanchez Fernandez"));
		knownPersons.put("Radityo Eko Prasojo", new Person("Radityo",
				"Eko Prasojo"));
		knownPersons.put("Jeff Z. Pan", new Person("Jeff Z.", "Pan"));
		knownPersons.put("Joao Rocha Da Silva", new Person("Joao",
				"Rocha Da Silva"));
		knownPersons.put("Joao Correia Lopes", new Person("Joao",
				"Correia Lopes"));
		knownPersons.put("Alexander Arturo Mera Caraballo", new Person(
				"Alexander Arturo", "Mera Caraballo"));
		knownPersons.put("Narciso Moura Arruda Junior", new Person("Narciso",
				"Moura Arruda Junior"));
		knownPersons.put("Bernardo Pereira Nunes", new Person("Bernardo",
				"Pereira Nunes"));
		knownPersons.put("Giseli Rabello Lopes", new Person("Giseli",
				"Rabello Lopes"));
		knownPersons.put("Marco Antonio Casanova", new Person("Marco Antonio",
				"Casanova"));
		knownPersons.put("Marco A. Casanova", new Person("Marco Antonio",
				"Casanova"));
		knownPersons.put("Axel-Cyrille Ngonga Ngomo", new Person(
				"Axel-Cyrille", "Ngonga Ngomo"));
		knownPersons.put("Pedro H. R. Assis", new Person("Pedro", "Assis"));
		knownPersons.put("Tom De Nies", new Person("Tom", "De Nies"));
		knownPersons.put("Laurens De Vocht", new Person("Laurens", "De Vocht"));
		knownPersons.put("Rik Van de Walle", new Person("Rik", "Van de Walle"));
		knownPersons.put("Jose Luis Redondo-Garcia", new Person("Jose Luis",
				"Redondo-Garcia"));
		knownPersons.put("Lilia Perez Romero", new Person("Lilia",
				"Perez Romero"));
		knownPersons.put("Daniela Espinoza Molina", new Person("Daniela",
				"Espinoza Molina"));
		knownPersons.put("Octavian Corneliu Dimitru", new Person("Octavian",
				"Corneliu Dimitru"));
		knownPersons.put("Octavian Corneliu Dimitru", new Person("Octavian",
				"Corneliu Dimitru"));
		knownPersons.put("Raul Garcia Castro", new Person("Raul",
				"Garcia Castro"));
		knownPersons.put("Miguel Esteban Gutierrez", new Person(
				"Miguel Esteban", "Gutierrez"));
		knownPersons.put("Angel Luis Garrido", new Person("Angel Luis",
				"Garrido"));
		knownPersons.put("Jose Eduardo Talavera Herrera", new Person(
				"Jose Eduardo", "Talavera Herrera"));
		knownPersons.put("Giseli Rabello Lopes", new Person("Giseli",
				"Rabello Lopes"));
		knownPersons.put("Maria Esther-Vidal", new Person("Maria Esther",
				"Vidal"));
		knownPersons.put("Maria Esther Vidal", new Person("Maria Esther",
				"Vidal"));
		knownPersons.put("Corneliu Valentin Stanciu", new Person(
				"Corneliu Valentin", "Stanciu"));
		// ****************end mashup

		// ****************research
		knownPersons.put("Chiara Di Francescomarino", new Person("Chiara",
				"Di Francescomarino"));
		knownPersons.put("Alessio Palmero Aprosio", new Person("Alessio",
				"Palmero Aprosio"));
		knownPersons.put("Norberto Fern??ndez Garc??a", new Person("Norberto",
				"Fern??ndez Garc??a"));
		knownPersons.put("Jes??s Arias Fisteus", new Person("Jes??s",
				"Arias Fisteus"));
		knownPersons.put("Luis S??nchez Fern??ndez", new Person("Luis",
				"S??nchez Fern??ndez"));
		knownPersons.put("Marco Luca Sbodio",
				new Person("Marco Luca", "Sbodio"));
		knownPersons.put("Axel-Cyrille Ngonga Ngomo", new Person(
				"Axel-Cyrille", "Ngonga Ngomo"));
		knownPersons.put("David C??lestin Faye", new Person("David C??lestin",
				"Faye"));
		knownPersons.put("Hugh C Davis", new Person("Hugh C.", "Davis"));
		knownPersons.put("Stuart N. Wrigley",
				new Person("Stuart N.", "Wrigley"));
		knownPersons.put("Angelo Di Iorio", new Person("Angelo", "Di Iorio"));
		knownPersons.put("Andrea Giovanni Nuzzolese", new Person(
				"Andrea Giovanni", "Nuzzolese"));
		knownPersons.put("Roberto De Virgilio", new Person("Roberto",
				"De Virgilio"));
		knownPersons.put("Jeff Z Pan", new Person("Jeff Z.", "Pan"));
		knownPersons.put("Kees Van Deemter", new Person("Kees", "Van Deemter"));
		knownPersons.put("Julio Cesar Dos Reis", new Person("Julio Cesar",
				"Dos Reis"));
		knownPersons.put("Marcos Da Silveira", new Person("Marcos",
				"Da Silveira"));
		knownPersons.put("Bernardo Pereira Nunes", new Person("Bernardo",
				"Pereira Nunes"));
		knownPersons.put("Marco Antonio Casanova", new Person("Marco Antonio",
				"Casanova"));
		// ****************end research

		// ****************mashup
		knownPersons.put("Miel Vander Sande",
				new Person("Miel", "Vander Sande"));
		knownPersons.put("Craig A. Knoblock", new Person("Craig A.",
				"Craig A. Knoblock"));
		knownPersons.put("Yael Ben Dov", new Person("Yael", "Ben Dov"));
		knownPersons.put("Simone Paolo Ponzetto", new Person("Simone Paolo",
				"Ponzetto"));
		knownPersons.put("Andrea Giovanni Nuzzolese", new Person(
				"Andrea Giovanni", "Nuzzolese"));
		knownPersons.put("Jesus Arias Fisteus", new Person("Jesus",
				"Arias Fisteus"));
		knownPersons.put("Norberto Fernandez Garcia", new Person("Jesus",
				"Arias Fisteus"));
		knownPersons.put("Luis Sanchez Fernandez", new Person("Luis",
				"Sanchez Fernandez"));
		knownPersons.put("Radityo Eko Prasojo", new Person("Radityo",
				"Eko Prasojo"));
		knownPersons.put("Jeff Z. Pan", new Person("Jeff Z.", "Pan"));
		knownPersons.put("Joao Rocha Da Silva", new Person("Joao",
				"Rocha Da Silva"));
		knownPersons.put("Joao Correia Lopes", new Person("Joao",
				"Correia Lopes"));
		knownPersons.put("Alexander Arturo Mera Caraballo", new Person(
				"Alexander Arturo", "Mera Caraballo"));
		knownPersons.put("Narciso Moura Arruda Junior", new Person("Narciso",
				"Moura Arruda Junior"));
		knownPersons.put("Bernardo Pereira Nunes", new Person("Bernardo",
				"Pereira Nunes"));
		knownPersons.put("Giseli Rabello Lopes", new Person("Giseli",
				"Rabello Lopes"));
		knownPersons.put("Marco Antonio Casanova", new Person("Marco Antonio",
				"Casanova"));
		knownPersons.put("Marco A. Casanova", new Person("Marco Antonio",
				"Casanova"));
		knownPersons.put("Axel-Cyrille Ngonga Ngomo", new Person(
				"Axel-Cyrille", "Ngonga Ngomo"));
		knownPersons.put("Pedro H. R. Assis", new Person("Pedro", "Assis"));
		knownPersons.put("Tom De Nies", new Person("Tom", "De Nies"));
		knownPersons.put("Laurens De Vocht", new Person("Laurens", "De Vocht"));
		knownPersons.put("Rik Van de Walle", new Person("Rik", "Van de Walle"));
		knownPersons.put("Jose Luis Redondo-Garcia", new Person("Jose Luis",
				"Redondo-Garcia"));
		knownPersons.put("Lilia Perez Romero", new Person("Lilia",
				"Perez Romero"));
		knownPersons.put("Daniela Espinoza Molina", new Person("Daniela",
				"Espinoza Molina"));
		knownPersons.put("Octavian Corneliu Dimitru", new Person("Octavian",
				"Corneliu Dimitru"));
		knownPersons.put("Octavian Corneliu Dimitru", new Person("Octavian",
				"Corneliu Dimitru"));
		knownPersons.put("Raul Garcia Castro", new Person("Raul",
				"Garcia Castro"));
		knownPersons.put("Miguel Esteban Gutierrez", new Person(
				"Miguel Esteban", "Gutierrez"));
		knownPersons.put("Angel Luis Garrido", new Person("Angel Luis",
				"Garrido"));
		knownPersons.put("Jose Eduardo Talavera Herrera", new Person(
				"Jose Eduardo", "Talavera Herrera"));
		knownPersons.put("Giseli Rabello Lopes", new Person("Giseli",
				"Rabello Lopes"));
		knownPersons.put("Maria Esther-Vidal", new Person("Maria Esther",
				"Vidal"));
		knownPersons.put("Maria Esther Vidal", new Person("Maria Esther",
				"Vidal"));
		knownPersons.put("Corneliu Valentin Stanciu", new Person(
				"Corneliu Valentin", "Stanciu"));
		// ****************end mashup

		// ****************challenges
		knownPersons.put("Jos?? Manuel G??mez-P??rez", new Person(
				"Jos?? Manuel", "G??mez-P??rez"));
		knownPersons.put("Gerard Casamayor Del Bosque", new Person("Gerard",
				"Casamayor Del Bosque"));
		knownPersons.put("Rik Van de Walle", new Person("Rik", "Van de Walle"));
		knownPersons.put("Miel Vander Sande",
				new Person("Miel", "Vander Sande"));
		knownPersons.put("Laurens De Vocht", new Person("Laurens", "De Vocht"));
		knownPersons.put("Eneldo Loza Mencia", new Person("Eneldo",
				"Loza Mencia"));
		knownPersons.put("Marco De Gemmis", new Person("Marco", "De Gemmis"));
		knownPersons.put("Christian Ariza-Porras", new Person("Christian",
				"Ariza-Porras"));
		knownPersons.put("Claudia Luc??a Jim??nez-Guar??n", new Person(
				"Claudia Luc??a", "Jim??nez-Guar??n"));
		knownPersons.put("Omar U. Florez", new Person("Omar U.", "Florez"));
		knownPersons.put("Celia Da Costa Pereira", new Person("Celia",
				"Da Costa Pereira"));
		knownPersons.put("Shafqat Mumtaz Virk", new Person("Shafqat Mumtaz",
				"Virk"));
		knownPersons.put("Jay Kuan-Chieh Chung", new Person("Jay Kuan-Chieh",
				"Chung"));
		knownPersons.put("Richard Tzong-Han Tsai", new Person(
				"Richard Tzong-Han", "Tsai"));
		// ****************end challenges

	}

}
