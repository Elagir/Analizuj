package com.jaar.services;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

public class CallService {

	public static final String NOT_FOUND = " ";

	public void callCeneoService(Client client, List<String> parsedCodes, String url, List<String> productNames,
			List<String> prices) {
		for (String code : parsedCodes) {

			String urlCeneo = url + code.replace(' ', '+');
			WebTarget target = client.target(getBaseURI(urlCeneo));

			String plainAnswer = target.request().accept(MediaType.TEXT_PLAIN).get(String.class);
			String price = NOT_FOUND;
			String title = NOT_FOUND;

			int startIndex = plainAnswer.indexOf("<strong class=\"price\"");
			if (startIndex >= 0) {
				int endIndex = plainAnswer.indexOf("</strong>", startIndex);
				if (endIndex >= 0) {
					price = plainAnswer.substring(startIndex + 23, endIndex);
					String plainAnswerSubstr = plainAnswer.substring(0, endIndex);
					int titleIndex = plainAnswerSubstr.lastIndexOf("title=");
					if (titleIndex >= 0) {
						title = plainAnswerSubstr.substring(titleIndex + 7, startIndex - 3);
					}
				}
			}
			productNames.add(title);
			prices.add(price);
			System.out.println(title + " " + price);
		}
	}

	public void callArmadeoService(Client client, List<String> parsedCodes, String url, 
			List<String> productNames, List<String> prices) {
		for (String code : parsedCodes) {

			String urlArmadeo = url + code.replace(' ', '+');
			WebTarget target = client.target(getBaseURI(urlArmadeo));

			String plainAnswer = target.request().accept(MediaType.TEXT_PLAIN).get(String.class);

			String price = NOT_FOUND;
			String title = NOT_FOUND;

			int startIndex = plainAnswer.indexOf("<span class=\"price\">");
			startIndex = plainAnswer.indexOf("<span class=\"price\">", startIndex + 20);
			if (startIndex >= 0) {

				int endIndex = plainAnswer.indexOf("</span>", startIndex);
				if (endIndex >= 0) {
					price = plainAnswer.substring(startIndex + 20, endIndex);
					String plainAnswerSubstr = plainAnswer.substring(0, endIndex);
					int titleIndex = plainAnswerSubstr.lastIndexOf("title=\"");
					if (titleIndex >= 0) {
						int endTitleIndex = plainAnswerSubstr.indexOf("\">", titleIndex);
						title = plainAnswerSubstr.substring(titleIndex + 7, endTitleIndex);
					}
				}
			}
			productNames.add(title);
			prices.add(price);
			System.out.println(title + " " + price);
		}
	}

	public void callJAARService(Client client, List<String> parsedCodes, String url, 
			List<String> productNames, List<String> prices) {

		for (String code : parsedCodes) {

			String urlJaar = url + code.replace(' ', '+');
			WebTarget target = client.target(getBaseURI(urlJaar));

			String plainAnswer = target.request().accept(MediaType.TEXT_PLAIN).get(String.class);

			String price = NOT_FOUND;
			String title = NOT_FOUND;

			int cenaIndex = plainAnswer.indexOf("<span>Cena:</span>");
			if (cenaIndex >= 0) {
				int startIndex = plainAnswer.indexOf("<em>", cenaIndex + 18);
				if (startIndex >= 0) {

					int endIndex = plainAnswer.indexOf("</em>", startIndex);
					if (endIndex >= 0) {
						price = plainAnswer.substring(startIndex + 4, endIndex);
						String plainAnswerSubstr = plainAnswer.substring(0, endIndex);
						int titleIndex = plainAnswerSubstr.lastIndexOf("<span class=\"productname\">");
						if (titleIndex >= 0) {
							int endTitleIndex = plainAnswerSubstr.indexOf("</span>", titleIndex);
							title = plainAnswerSubstr.substring(titleIndex + 26, endTitleIndex);
						}
					}
				}
			}
			productNames.add(title);
			prices.add(price);
			System.out.println(title + " " + price);
		}

	}

	private static URI getBaseURI(String url) {
		return UriBuilder.fromUri(url).build();
	}

}
