package com.kontur.shortener.dao;

import com.kontur.shortener.model.Link;

import java.util.List;

public interface LinkDao {
   Link save(Link link);
   Link getByShortLink(String shortLink);
   Link getByOriginalLink(String originalLink);
   void countIncrement(Link link);
   List<Link> list(int begin , int count);

}
