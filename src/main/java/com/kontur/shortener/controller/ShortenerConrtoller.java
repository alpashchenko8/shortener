package com.kontur.shortener.controller;

import com.kontur.shortener.dao.LinkDao;
import com.kontur.shortener.model.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class ShortenerConrtoller {
    @Autowired
    LinkDao linkDao;

    @RequestMapping(
            path = "generate",
            method = RequestMethod.POST
    )
    public ResponseEntity generate(@RequestParam("original") String original){
        Link link = new Link(original);
        link=linkDao.save(link);
        String response = "{ \"link\":\"/l/"+ link.getSortLink()+"\"}";
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(
            path = "/l/*",
            method = RequestMethod.GET
    )
    public String linkShortUrl(HttpServletRequest request , HttpServletResponse response /*, @RequestHeader("path") String path*/) throws IOException {
        String path =request.getRequestURI();
        String shortLink = path.substring(3);
        if(!shortLink.isEmpty()){
            Link link = linkDao.getByShortLink(shortLink);
            if(link!=null){
                String originalLink = link.getOriginalLink();
                linkDao.countIncrement(link);
                return "redirect:" + originalLink;}

        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return null;
    }

    @RequestMapping(
            path = "/stats/*",
            method = RequestMethod.GET
    )
    public ResponseEntity<String> statsShortUrl(HttpServletRequest request /*, @RequestHeader("path") String path*/){
        //  System.out.println(path);
        String path = request.getRequestURI();
        String shortLink = path.substring(7);
        if(!shortLink.isEmpty()) {
            Link link = linkDao.getByShortLink(shortLink);
            if (link != null) {
                String jsonString = link.toJsonString();
                return ResponseEntity.ok().body(jsonString);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(
            path = "/stats",
            method = RequestMethod.GET
    )
    public ResponseEntity<String> statsPage(@RequestParam("page") Integer page ,@RequestParam("count") Integer count ){
        int begin = ((page-1)*count);
        List<Link> listLink = linkDao.list(begin,count);
        StringBuilder stringBuilder = new StringBuilder();
        if ((listLink!=null)&&(!listLink.isEmpty())) {
            stringBuilder.append("[");
            for (Link link : listLink){
                stringBuilder.append(link.toJsonString());
                stringBuilder.append(",");
            }
            stringBuilder.setLength(stringBuilder.length()-1);
            stringBuilder.append("]");
        }
        String result = stringBuilder.toString();

        return ResponseEntity.ok().body(result);


    }
}
