package com.cem.valven.service;


import com.cem.valven.dto.Diff;
import com.cem.valven.dto.GitLabCommit;
import com.cem.valven.entity.Commit;
import com.cem.valven.entity.Developer;
import com.cem.valven.exception.CommitMappingException;
import com.cem.valven.exception.GitLabApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class GitLabService {


    private static final String GITLAB_API_URL = "https://gitlab.com/api/v4/projects/{projectId}/repository/commits";
    private final String GITLAB_COMMIT_DETAIL_URL = "https://gitlab.com/api/v4";

    private final RestTemplate restTemplate;
    private final CommitService commitService;
    private final DeveloperService developerService;


    public void fetchAndSaveCommits() {
        List<Commit> gitLabCommits = fetchCommitsFromGitLab("42817607");
        commitService.saveAll(gitLabCommits);

    }

    public List<Commit> fetchCommitsFromGitLab(String projectId) {
        try {


            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            String since = thirtyDaysAgo.format(DateTimeFormatter.ISO_DATE_TIME);

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(GITLAB_API_URL)
                    .queryParam("since", since);

            GitLabCommit[] commits = restTemplate.getForObject(builder.buildAndExpand(projectId).toUri(), GitLabCommit[].class);

            return mapGitLabCommitsToEntities(commits, projectId);
        } catch (Exception e) {
            throw new GitLabApiException("Error fetching commits from GitLab. " + e.getMessage());
        }
    }

    private List<Commit> mapGitLabCommitsToEntities(GitLabCommit[] commits, String projectId) {
        List<Commit> commitEntities = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        for (GitLabCommit commit : commits) {
            try {


                Commit entity = new Commit();
                entity.setHash(commit.getId());
                entity.setTimestamp(Timestamp.valueOf(LocalDateTime.parse(commit.getCommittedDate(), formatter)));
                entity.setMessage(commit.getMessage());
                entity.setAuthor(commit.getAuthorName());
                entity.setPatch("");


                String username = commit.getAuthorName();
                Developer developer = developerService.getDeveloperByUsername(username)
                        .orElseGet(() -> {
                            Developer newDeveloper = new Developer();
                            newDeveloper.setUsername(username);
                            newDeveloper.setEmail(commit.getAuthorEmail());
                            return developerService.saveDeveloper(newDeveloper);
                        });

                entity.setDeveloper(developer);

                // Fetch patch details
                try {


                    String url = GITLAB_COMMIT_DETAIL_URL + "/projects/" + projectId + "/repository/commits/" + commit.getId() + "/diff";
                    ResponseEntity<Diff[]> response = restTemplate.getForEntity(url, Diff[].class);
                    StringBuilder patch = new StringBuilder();
                    for (Diff diff : response.getBody()) {
                        patch.append(diff.getDiff()).append("\n");
                    }
                    entity.setPatch(patch.toString());

                    commitEntities.add(entity);
                } catch (Exception e) {
                    throw new GitLabApiException("Error fetching commit details from GitHub. " + e.getMessage());
                }
            } catch (Exception e) {
                throw new CommitMappingException("Error mapping GitLab commit to entity. " + e.getMessage());
            }
        }
        return commitEntities;
    }


}
