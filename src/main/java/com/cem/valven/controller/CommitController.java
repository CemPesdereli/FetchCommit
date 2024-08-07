package com.cem.valven.controller;

import com.cem.valven.entity.Commit;
import com.cem.valven.service.CommitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommitController {

    private final CommitService commitService;

    @GetMapping("/commits/{username}")
    public String getCommitsByDeveloper(@PathVariable String username, Model model) {
        List<Commit> commits = commitService.getCommitsByDeveloper(username);
        model.addAttribute("commits", commits);
        return "commit-list";
    }

    @GetMapping("/commit/{hash}")
    public String getCommitDetails(@PathVariable String hash, Model model) {
        Commit commit = commitService.getCommitById(hash).orElseThrow( ()-> new RuntimeException("Commit not found"));
        model.addAttribute("commit", commit);
        return "commit-details";
    }
}
