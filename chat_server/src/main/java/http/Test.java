package http;

import com.fasterxml.jackson.core.JsonProcessingException;
import http.TestMethodService;
import com.lee.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/test")
public class Test {

    @Autowired
    private TestMethodService testMethodService;

//    @RequestMapping("/test2")
//    private String test2(){
//        return "1233";
//    }


    @RequestMapping(value = "/test",method = RequestMethod.POST)
    private Message test(@RequestBody String s) throws JsonProcessingException {


        //测试
        System.out.println(s);
        return testMethodService.test(s);
    }



}
