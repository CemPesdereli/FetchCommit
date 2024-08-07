package com.cem.valven.repository;

import com.cem.valven.entity.Commit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommitRepository extends JpaRepository<Commit,String> {

    List<Commit> findByDeveloperUsername(String username);
}
