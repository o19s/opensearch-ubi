package com.o19s.opensearchbackend;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@Controller
public class BackendController {

    @PostMapping("/log")
    @ResponseBody
    public String log(@RequestBody String body) throws IOException {

        final File file = new File("/tmp/opensearch.log");
        FileUtils.writeStringToFile(file, body, Charset.defaultCharset(), true);

        System.out.println(body);

        return "logged";

    }

}