package com.yb.practice;

import java.util.LinkedList;
import java.util.List;

public class NumberToLetters {

	/**
	 * 队列思想，利用队列先进先出，将之前的字符串出队，拼接新的字符，新生成的字符串入队
	 * 
	 * @author YANGB
	 * @param digits
	 * @return List
	 */
	public List<String> combination(int[] arr) {
		LinkedList<String> ans = new LinkedList<String>();
		if (arr.length == 0 || arr == null)
			return ans;
		// 定义数组到字母的映射关系数组
		String[] mapping = new String[] { "0", "1", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz" };
		ans.add("");
		for (int i = 0; i < arr.length; i++) {
			String str = "";
			int length = String.valueOf(arr[i]).length();
			if (length > 1) {
				char[] chs = String.valueOf(arr[i]).toCharArray();
				for (char c : chs) {
					str += mapping[Integer.parseInt(String.valueOf(c))];
				}
			} else {
				str += mapping[arr[i]];
			}
			while (ans.peek().length() == i) {
				// 取到队列中第一个并移除掉
				String t = ans.remove();
				for (char s : str.toCharArray())
					// 进行字符拼接
					ans.add(t + s);
			}
		}
		return ans;
	}
}
