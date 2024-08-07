package com.cem.valven.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GitHubCommit {
    private String sha;
    private Commit commit;
    private File[] files;


    @Getter
    @Setter
    public static class Commit {
        private Author author;
        private String message;


        @Getter
        @Setter
        public static class Author {
            private String name;
            private String email;
            private String date;


        }


    }

    @Getter
    @Setter
    public static class File {
        private String filename;
        private String patch;


    }


}
