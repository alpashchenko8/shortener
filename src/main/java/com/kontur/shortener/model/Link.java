package com.kontur.shortener.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String originalLink;
    private long count;
    private long rank;

    public Link(String originalLink) {
        this.originalLink = originalLink;
        this.count = 0L;
    }

    public Link() { }

    public String getSortLink(){
        return Long.toString(id,36);
    }



    public Long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }
    @Override
    public String toString() {
        return "Link{" + "id=" + id + "," +
                " original='" + originalLink + '\'' + "," +
                " count=" + count + "," +
                " rank=" + rank + '}';
    }
    public String toJsonString() {
        return  "{\"link\": \"/l/" + getSortLink() + "\"," +
                "\"original\": \"" + originalLink + "\"," +
                "\"rank\": " + rank + "," +
                "\"count\": " + count + "}";
    }
}
