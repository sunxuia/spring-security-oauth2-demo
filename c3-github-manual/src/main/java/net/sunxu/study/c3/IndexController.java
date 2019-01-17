package net.sunxu.study.c3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class IndexController {
    @RequestMapping("/")
    public ModelAndView index(Principal principal) throws JsonProcessingException {
        String str = "";
        if(principal != null) {
            ObjectMapper mapper = new ObjectMapper();
            str = mapper.writeValueAsString(principal);
        }
        return new ModelAndView("index", "principal", str);
    }
}
