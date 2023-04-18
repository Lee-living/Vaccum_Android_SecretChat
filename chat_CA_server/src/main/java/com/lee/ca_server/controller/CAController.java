package com.lee.ca_server.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/ca")
public class CAController {
    private final HashMap<String, String> userKeyMap = new HashMap<>();

    @PostMapping("/registerPublicKey")
    public String registerPublicKey(@RequestParam String username, @RequestParam String publicKeyStr) {
        //接收到消息后，将用户名和公钥存入map中
        userKeyMap.put(username, publicKeyStr);
        System.out.println(username);
        System.out.println(publicKeyStr);
        return "Success";
    }

    @GetMapping("/getPublicKey")
    public String getPublicKey(@RequestParam String username) {
        //接收到消息后，根据用户名从map中取出公钥
        return userKeyMap.get(username);
    }
}
