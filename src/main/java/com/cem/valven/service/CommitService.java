package com.cem.valven.service;

import com.cem.valven.entity.Commit;
import com.cem.valven.repository.CommitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommitService {

    private final CommitRepository commitRepository;

    public List<Commit> getCommitsByDeveloper(String username) {
        return commitRepository.findByDeveloperUsername(username);
    }

    public Commit saveCommit(Commit commit) {
        return commitRepository.save(commit);
    }
    public void saveAll(List<Commit> commits) {
        commitRepository.saveAll(commits);
    }

    public Optional<Commit> getCommitById(String hash) {
        return commitRepository.findById(hash);
    }
}
