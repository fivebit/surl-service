package com.fivebit.controller;

import com.fivebit.components.Slog;
import com.fivebit.components.Sredis;
import com.fivebit.filters.AppException;
import com.fivebit.service.ShortUrlService;
import com.fivebit.utils.Constants;
import com.fivebit.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by fivebit on 2017/7/12.
 */
@RestController
@RequestMapping(value = "/s", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ShortUrlController {
    @Autowired
    private Slog slog;
    @Autowired
    private ShortUrlService shortUrlService;

    @GetMapping("/{code}")
    public ResponseEntity redirectShortUrl(@PathVariable("code") String code) throws AppException {
        slog.info(" redirectShortUrl begin:" + code);
        if (Utils.checkString(code, Constants.CODE_LENGTH_MAX) == false) {
            throw new AppException("0", "code format error");
        }
        String org_url = shortUrlService.getOrgUrlByCode(code);
        if (org_url == null) {
            throw new AppException("0", "cant find surl-service by:" + code);
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(org_url)).build();
    }

    @PostMapping("")
    public ResponseEntity createShortUrl() {

        return null;
    }

    @GetMapping("/gen")
    public ResponseEntity createShortUrlByGet(@RequestParam(value = "access_token", required = true) String access_token,
                                              @RequestParam(value = "url_long") List<String> urls_long ) throws AppException {
        slog.info("createShortUrlByGet ak :" + access_token + " urls:" + urls_long);
        if (access_token.equals(Constants.ACCESS_TOKEN) == false) {
            throw new AppException("0", "NO AUTH");
        }
        List<Map<String, String>> short_urls = shortUrlService.makeShortUrl(urls_long);
        slog.info("create short surl-service end:return:" + short_urls.toString());
        return ResponseEntity.ok(Utils.getRespons(short_urls));
    }

    @GetMapping("times/{code}")
    public ResponseEntity getClickTimes(@PathVariable("code") String code) throws AppException {
        return ResponseEntity.ok(Utils.getRespons(shortUrlService.getClickTimes(code)));
    }
}
