/**
 * Copyright 2008-2015, Province of British Columbia
 * All rights reserved.
 */
package ca.bc.gov.ols.util;

public class StringUtils {
	
	public static String join(String[] strings, String delimiter) {
		StringBuilder sb = new StringBuilder();
		int i;
		for(i = 0; i < strings.length - 1; i++) {
			sb.append(strings[i] + delimiter);
		}
		return sb.toString() + strings[i];
	}
	
	/*
	 * Minor optimization to prevent slow calculation of OSADistance when there is no chance of the
	 * strings being within a given tolerance. There are other optimizations for the case of a known
	 * tolerance but they are more complicated to implement and our locality names are relatively
	 * short so it's probably not worth it to implement.
	 */
	public static int OSADistanceWithLimit(String source, String target, int limit) {
		if(Math.abs(source.length() - target.length()) <= limit) {
			return OSADistance(source, target);
		}
		return limit + 1;
	}
	
	/* Optimal String Alignment Distance */
	public static int OSADistance(String source, String target) {
		if(source == null || source.isEmpty()) {
			if(target == null || target.isEmpty()) {
				return 0;
			} else {
				return target.length();
			}
		} else if(target == null || target.isEmpty()) {
			return source.length();
		}
		source = source.toUpperCase();
		target = target.toUpperCase();
		
		int n = source.length();
		int m = target.length();
		int[][] d = new int[n + 1][m + 1];
		
		for(int i = 0; i <= n; i++) {
			d[i][0] = i;
		}
		for(int j = 0; j <= m; j++) {
			d[0][j] = j;
		}
		
		for(int i = 1; i <= n; i++) {
			for(int j = 1; j <= m; j++) {
				int cost = 0;
				if(source.charAt(i - 1) != target.charAt(j - 1)) {
					cost = 1;
				}
				d[i][j] = Math.min(d[i - 1][j] + 1,
						Math.min(d[i][j - 1] + 1, d[i - 1][j - 1] + cost));
				if(i > 1 && j > 1 && source.charAt(i - 1) == target.charAt(j - 2)
						&& source.charAt(i - 2) == target.charAt(j - 1)) {
					d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + cost);
				}
			}
		}
		return d[n][m];
	}
	
	public static String capitalizeFirst(String phrase) {
		return phrase.substring(0, 1).toUpperCase() + phrase.substring(1).toLowerCase();
	}
	
	public static String defaultString(String str) {
		if(str == null) {
			return "";
		}
		return str;
	}
}
