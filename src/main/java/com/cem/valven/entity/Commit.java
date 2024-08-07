package com.cem.valven.entity;


import jakarta.persistence.*;

import lombok.*;

import java.sql.Timestamp;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Commit {
    @Id
    private String hash;
    private Timestamp timestamp;  // which package?
    private String message;
    private String author;
    @Column(columnDefinition = "TEXT")
    private String patch;

    @ManyToOne
    @JoinColumn(name = "developer_id")
    private Developer developer;

}
