package com.yb.practice;

import java.util.List;

import junit.framework.TestCase;

public class NumberToLettersTest extends TestCase {

	public void testCombination() {
		int[] arr = {2, 3, 4};
		NumberToLetters obj = new NumberToLetters();
		List<String> resultList = obj.combination(arr);
		for (String str : resultList) {
			System.out.println(str);
		}
	}
	
	public void testCombination1() {
		int[] arr = {2, 36, 47};
		NumberToLetters obj = new NumberToLetters();
		List<String> resultList = obj.combination(arr);
		for (String str : resultList) {
			System.out.println(str);
		}
	}
	
	public void testCombination2() {
		int[] arr = {2, 36, 99};
		NumberToLetters obj = new NumberToLetters();
		List<String> resultList = obj.combination(arr);
		for (String str : resultList) {
			System.out.println(str);
		}
	}
}
