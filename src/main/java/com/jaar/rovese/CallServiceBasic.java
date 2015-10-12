package com.jaar.rovese;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

public class CallServiceBasic {

	public static final String NOT_FOUND = " ";

	public void callCeneoService(Client client, List<String> parsedCodes, String url, 
			List<String> prices) {
		for (String code : parsedCodes) {

			String urlCeneo = url + code.replace(' ', '+');
			WebTarget target = client.target(getBaseURI(urlCeneo));

			String plainAnswer = target.request().accept(MediaType.TEXT_PLAIN).get(String.class);
			String price = NOT_FOUND;

			int startIndex = plainAnswer.indexOf("<strong class=\"price\"");
			if (startIndex >= 0) {
				int endIndex = plainAnswer.indexOf("</strong>", startIndex);
				if (endIndex >= 0) {
					price = plainAnswer.substring(startIndex + 23, endIndex);
					price = price.replaceAll("[^0-9,.]", "");
				}
			}
			prices.add(price);
//			System.out.println("Ceneo" + " " + price);
		}
	}

	public void callArmadeoService(Client client, List<String> parsedCodes, String url, 
			List<String> prices) {
		for (String code : parsedCodes) {

			String urlArmadeo = url + code.replace(' ', '+');
			WebTarget target = client.target(getBaseURI(urlArmadeo));

			String plainAnswer = target.request().accept(MediaType.TEXT_PLAIN).get(String.class);

			String price = NOT_FOUND;

			int startIndex = plainAnswer.indexOf("<span class=\"price\">");
			startIndex = plainAnswer.indexOf("<span class=\"price\">", startIndex + 20);
			if (startIndex >= 0) {

				int endIndex = plainAnswer.indexOf("</span>", startIndex);
				if (endIndex >= 0) {
					price = plainAnswer.substring(startIndex + 20, endIndex);
					price = price.replaceAll("[^0-9,.]", "");
				}
			}
			prices.add(price);
//			System.out.println("Armadeo" + " " + price);
		}
	}

	public void callJAARService(Client client, List<String> parsedCodes, String url, 
			List<String> prices) {

		for (String code : parsedCodes) {

			String urlJaar = url + code.replace(' ', '+');
			WebTarget target = client.target(getBaseURI(urlJaar));
			String plainAnswer = target.request().accept(MediaType.TEXT_PLAIN).get(String.class);

			String price = NOT_FOUND;

			int cenaIndex = plainAnswer.indexOf(">Cena:</span>");
			if (cenaIndex >= 0) {
				int startIndex = plainAnswer.indexOf("<em>", cenaIndex + 18);
				if (startIndex >= 0) {

					int endIndex = plainAnswer.indexOf("</em>", startIndex);
					if (endIndex >= 0) {
						price = plainAnswer.substring(startIndex + 4, endIndex);
						price = price.replaceAll("[^0-9,.]", "");
					}
				}
			}
			prices.add(price);
			System.out.println("JAAR" + " " + price);
		}

	}

	private static URI getBaseURI(String url) {
		return UriBuilder.fromUri(url).build();
	}

}
