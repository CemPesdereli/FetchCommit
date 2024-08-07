package com.cem.valven.service;

import com.cem.valven.dto.GitHubCommit;
import com.cem.valven.entity.Commit;
import com.cem.valven.entity.Developer;
import com.cem.valven.exception.CommitMappingException;
import com.cem.valven.exception.GitHubApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubService {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/{owner}/{repo}/commits";
    private static final String GITHUB_COMMIT_DETAIL_URL = "https://api.github.com/repos/{owner}/{repo}/commits/{sha}";


    private final RestTemplate restTemplate;

    private final CommitService commitService;
    private final DeveloperService developerService;


    public void fetchAndSaveCommits() {
        List<Commit> gitHubCommits = fetchCommitsFromGitHub("CemPesdereli", "E-Commerce-App");
        List<Commit> gitHubCommits2 = fetchCommitsFromGitHub("duhanboblanli", "DOTNET8-MVC-E-Commerce-Web-App");
        List<Commit> gitHubCommits3 = fetchCommitsFromGitHub("GoktayIncekara", "Picnic-Bag-App");
        commitService.saveAll(gitHubCommits);
        commitService.saveAll(gitHubCommits2);
        commitService.saveAll(gitHubCommits3);

    }

    public List<Commit> fetchCommitsFromGitHub(String owner, String repo) {
        try {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);  // only CemPesdereli has commits in the past 30 days, to check other profiles you can set minus days to 900.
            String since = thirtyDaysAgo.format(DateTimeFormatter.ISO_DATE_TIME);

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(GITHUB_API_URL)
                    .queryParam("since", since);

            GitHubCommit[] commits = restTemplate.getForObject(builder.buildAndExpand(owner, repo).toUri(), GitHubCommit[].class);

            return mapGitHubCommitsToEntities(commits, owner, repo);
        } catch (Exception e) {
            throw new GitHubApiException("Error fetching commits from GitHub. " + e.getMessage());
        }
    }

    private List<Commit> mapGitHubCommitsToEntities(GitHubCommit[] commits, String owner, String repo) {
        List<Commit> commitEntities = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        for (GitHubCommit commit : commits) {
            try {


                Commit entity = new Commit();
                entity.setHash(commit.getSha());
                entity.setTimestamp(Timestamp.valueOf(LocalDateTime.parse(commit.getCommit().getAuthor().getDate(), formatter)));
                entity.setMessage(commit.getCommit().getMessage());
                entity.setAuthor(commit.getCommit().getAuthor().getName());


                String username = commit.getCommit().getAuthor().getName();
                Developer developer = developerService.getDeveloperByUsername(username)
                        .orElseGet(() -> {
                            Developer newDeveloper = new Developer();
                            newDeveloper.setUsername(username);
                            newDeveloper.setEmail(commit.getCommit().getAuthor().getEmail());
                            return developerService.saveDeveloper(newDeveloper);
                        });

                entity.setDeveloper(developer);
                // Fetch patch details
                try {


                    GitHubCommit commitDetail = restTemplate.getForObject(GITHUB_COMMIT_DETAIL_URL, GitHubCommit.class, owner, repo, commit.getSha());
                    StringBuilder patchBuilder = new StringBuilder();
                    for (GitHubCommit.File file : commitDetail.getFiles()) {
                        patchBuilder.append(file.getPatch()).append("\n");
                    }
                    entity.setPatch(patchBuilder.toString());

                    commitEntities.add(entity);
                } catch (Exception e) {
                    throw new GitHubApiException("Error fetching commit details from GitHub. " + e.getMessage());
                }
            } catch (Exception e) {
                throw new CommitMappingException("Error mapping GitHub commit to entity. " + e.getMessage());
            }
        }
        return commitEntities;
    }


}

