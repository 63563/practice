package com.yb.practice;

import java.util.LinkedList;
import java.util.List;

public class NumberToLetters {

	/**
	 * 队列思想，利用队列先进先出，将之前的字符串出队，拼接新的字符，新生成的字符串入队
	 * @author YANGB
	 * @param digits
	 * @return List
	 */
	public List<String> combination(int[] arr) {
		//将数组转换字符串
		String str ="";
		for (int i = 0; i < arr.length; i++) {
			str += arr[i];
		}
		
		LinkedList<String> ans = new LinkedList<String>();
		if (str.isEmpty())
			return ans;
		//定义数组到字母的映射关系数组
		String[] mapping = new String[] { "0", "1", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz" };
		ans.add("");
		for (int i = 0; i < str.length(); i++) {
			//将字符解析成数字
			int x = Character.getNumericValue(str.charAt(i));
			//判断取出来的是不是字符,不是字符就不进循环
			while (ans.peek().length() == i) {
				//取到队列中第一个并移除掉
				String t = ans.remove();
				for (char s : mapping[x].toCharArray())
					//进行字符拼接
					ans.add(t + s);
			}
		}
		return ans;
	}
}
