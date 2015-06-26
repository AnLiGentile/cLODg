package org.eswc.conferences.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ContentHandlerFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Sentiment {

	public double evaluate(String text) {
		double ret = 0.0;

		try {
			text = URLEncoder.encode(text, "UTF-8");

			URLConnection connection = new URL(
					"http://isotta.cs.unibo.it:8181/?callback=feedback&text="
							+ text).openConnection();
			InputStream inputStream = connection.getInputStream();

			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(isr);
			String json = "";
			String line = null;
			while ((line = br.readLine()) != null) {
				json += line;
			}

			json = json.substring("feedback(".length(), json.length() - 1);

			JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
			ret = (Double) jsonObject.get("Score");
			return ret;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	public static void main(String[] args) {
		new Sentiment().evaluate("bad presentation of paper 2");
	}

}
