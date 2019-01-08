package com.sheena.playground.client;

import java.util.Scanner;

import org.springframework.web.client.RestTemplate;

public class PlaygroundDemoClient {

	private RestTemplate rest;
	private String host;
	private int port;
	private Scanner s;

	public PlaygroundDemoClient() {
	}

	public PlaygroundDemoClient(String host, int port) {
		this.rest = new RestTemplate();
		this.host = host;
		this.port = port;
		this.s = new Scanner(System.in);
	}
	
	// change it!
	// -Dplayground.port=8083 -Dplayground.host=172.20.54.120
	public static void main(String[] args) {
		String host = System.getProperty("playground.host");
		if (host == null) {
			host = "localhost";
		}

		int port;
		try {
			port = Integer.parseInt(System.getProperty("playground.port"));
		} catch (Exception e) {
			port = 8080;
		}
		
		PlaygroundDemoClient client = new PlaygroundDemoClient(host, port);
		client.registerToPlayground();
		
	}

	private void registerToPlayground() {
		String email;
		String username;
		String avatar;
		String role;
		
		System.out.println("Hello, welcome to shift management system");
		System.out.println("Please enter your email:");
		email = s.nextLine();
		System.out.println("Please choose an username:");
		username = s.nextLine();
		System.out.println("Please choose an avatar:");
		avatar = s.nextLine();
		
	}

}
