package cn.edcheung.springskills.gateway.resttemplate.controller;


import cn.edcheung.springskills.gateway.resttemplate.service.RedirectService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description RedirectController
 *
 * @author Edward Cheung
 * @date 2021/11/8
 * @since JDK 1.8
 */
@RestController
public class RedirectController {

    @Resource
    private RedirectService redirectService;

    @RequestMapping(path = "/**", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> redirect(HttpServletRequest request, HttpServletResponse response) {
        return redirectService.redirect(request, response, "http://localhost:8080", "/restTemplate");
    }
}
