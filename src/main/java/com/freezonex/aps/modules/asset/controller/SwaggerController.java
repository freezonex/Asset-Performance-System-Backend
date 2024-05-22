package com.freezonex.aps.modules.asset.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/apsbackend")
@ApiIgnore // 这将忽略整个Controller
public class SwaggerController {


    @GetMapping("/doc.html")
    public String index() {
        return "forward:/doc.html";
    }

    @GetMapping("/swagger-resources/configuration/ui")
    public String configurationUI() {
        return "forward:/swagger-resources/configuration/ui";
    }

    @GetMapping("/swagger-resources")
    public String resources() {
        return "forward:/swagger-resources";
    }

    @GetMapping("/v2/api-docs")
    public String docs() {
        return "forward:/v2/api-docs";
    }
}