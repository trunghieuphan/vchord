package com.hatgroup.vchord.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hatgroup.vchord.common.Constants;

public class Parser {

	private static String space = "\\s+";
	// Use lazy matching (reluctant) to match as less as possible
	private static String chord = "\\[.+?\\]";
	private static String emptyChord = "(\\[\\])?";
	private String inputString;

	public Parser(String inputString) {
		if (inputString == null) {
			throw new Error("Parsed string cannot be null");
		}
		this.inputString = inputString.trim();
	}

	public static String readFileToString() {
		StringBuilder sb = new StringBuilder();
		InputStream is = Parser.class.getResourceAsStream("nhac.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				sb.append(line);
				// Append new line char to make anchor for new line
				sb.append(Constants.NEW_LINE_TOKEN);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public CopyOnWriteArrayList<ParsedObject> doParse() {
		CopyOnWriteArrayList<ParsedObject> results = new CopyOnWriteArrayList<ParsedObject>();
		// Pattern p = Pattern.compile(chord + "|" + space + "|" + "("+
		// Constants.NEW_LINE_TOKEN +")");
		Pattern p = Pattern.compile(chord + "|" + space);
		Matcher m = p.matcher(inputString);
		int preIndex = 0;
		LinkedList<String> tokens = new LinkedList<String>();
		LinkedList<String> words = new LinkedList<String>();
		while (m.find()) {
			int start = m.start();
			String token = m.group();
			int end = m.end();
			String word = inputString.substring(preIndex, start);
			tokens.add(token);
			words.add(word);
			preIndex = end;
		}
		if (preIndex < inputString.length()) {
			tokens.add("");
			words.add(inputString.substring(preIndex, inputString.length()));
		}
		for (int i = 0; i < words.size(); i++) {
			if (i == 0) {
				results.add(new ParsedObject(" ", words.get(i)));
			} else {
				results.add(new ParsedObject(tokens.get(i - 1), words.get(i)));
			}
		}
		return tailor(results);
	}

	public CopyOnWriteArrayList<ParsedObject> parseData() {
		CopyOnWriteArrayList<ParsedObject> results = new CopyOnWriteArrayList<ParsedObject>();
		String[] lines = {};
		if (inputString.indexOf(Constants.NEW_LINE_TOKEN) != -1) {
			lines = inputString.split(Constants.NEW_LINE_TOKEN);
		} else {
			lines = inputString.split(Constants.NEW_LINE_TOKEN2);
		}

		for (int lineNum = 0; lineNum < lines.length; lineNum++) {
			String line = lines[lineNum].trim();
			if (line.equals("")) {
				continue;
			}
			CopyOnWriteArrayList<ParsedObject> objects = parseLine(line);
			// From the second line, should mark as new line
			if (lineNum > 0) {
				objects.get(0).markAsNewLine();
			}
			results.addAll(objects);
		}
		return results;
	}

	private CopyOnWriteArrayList<ParsedObject> parseLine(String line) {
		CopyOnWriteArrayList<ParsedObject> results = new CopyOnWriteArrayList<ParsedObject>();
		Pattern p = Pattern.compile(chord + "|" + space);
		Matcher m = p.matcher(line);
		int preIndex = 0;
		LinkedList<String> tokens = new LinkedList<String>();
		LinkedList<String> words = new LinkedList<String>();
		while (m.find()) {
			int start = m.start();
			String token = m.group();
			int end = m.end();
			String word = line.substring(preIndex, start);
			tokens.add(token);
			words.add(word);
			preIndex = end;
		}
		if (preIndex < line.length()) {
			tokens.add("");
			words.add(line.substring(preIndex, line.length()));
		}
		for (int i = 0; i < words.size(); i++) {
			if (i == 0) {
				results.add(new ParsedObject(" ", words.get(i)));
			} else {
				results.add(new ParsedObject(tokens.get(i - 1), words.get(i)));
			}
		}
		return tailor(results);
	}

	private CopyOnWriteArrayList<ParsedObject> tailor(
			CopyOnWriteArrayList<ParsedObject> list) {
		String shiftedToken = "";
		for (ParsedObject po : list) {
			if (po.isEmtyObject()) {
				list.remove(po);
				continue;
			}
			// receive the shifted token and reset it
			if (!shiftedToken.equals("")) {
				po.token = shiftedToken + po.trimmedToken();
				shiftedToken = "";
			}
			// Shift token to the next object if the word is blank, remove the
			// object after shifted
			if (po.trimmedToken().matches(chord) && po.trimmedWord().equals("")) {
				shiftedToken = po.trimmedToken();
				list.remove(po);
				continue;
			}
		}
		return list;
	}

	public static class ParsedObject {
		public String token = "";
		public String word = "";

		public ParsedObject(String token, String word) {
			this.token = token;
			this.word = word;
		}

		public String trimmedToken() {
			// Preserve new line chars
			// if(token.startsWith(Constants.NEW_LINE_TOKEN)){
			// return Constants.NEW_LINE_TOKEN + token.trim();
			// }
			return token.trim();
		}

		public String trimmedWord() {
			return word.trim();
		}

		public boolean isEmtyObject() {
			return token.trim().equals("") && word.trim().equals("");
		}

		public String toString() {
			return String.format("%s %s", token, word);
		}

		public boolean hasNewLine() {
			// return token.contains(Constants.NEW_LINE_TOKEN)
			// || word.contains(Constants.NEW_LINE_TOKEN);
			return isNewLine
					|| token.contains(Constants.REVERSE_NEW_LINE_TOKEN)
					|| word.contains(Constants.REVERSE_NEW_LINE_TOKEN)
					|| token.contains(Constants.NEW_LINE_TOKEN)
					|| word.contains(Constants.NEW_LINE_TOKEN)
					|| token.contains(Constants.NEW_LINE_TOKEN2)
					|| word.contains(Constants.NEW_LINE_TOKEN2);
		}

		private boolean isNewLine;

		public void markAsNewLine() {
			isNewLine = true;
		}
	}
}
