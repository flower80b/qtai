package com.qai.mvc.controller;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//0.2345 -> 23.45    0.0345 --> 
		String a = "0.12345";//.substring(0, );
		//int b = (int) (Double.valueOf(a) *100);
		//String aa = String.format("%.2", Double.valueOf(a) *100);
		System.out.println(String.format("{0:0.0#}", Double.valueOf(a) *100));
		System.out.println(String.format("%.2f", Double.valueOf(a) *100));
	}

}
