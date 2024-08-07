package com.cem.valven;

import com.cem.valven.service.GitHubService;
import com.cem.valven.service.GitLabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ValvenApplication implements CommandLineRunner {

	@Autowired
	private GitHubService gitHubService;

	@Autowired
	private GitLabService gitLabService;

	public static void main(String[] args) {
		SpringApplication.run(ValvenApplication.class, args);
	}

	@Override
	public void run(String... args) {
		gitHubService.fetchAndSaveCommits();
		gitLabService.fetchAndSaveCommits();
	}

}
