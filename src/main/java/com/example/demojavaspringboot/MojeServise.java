package com.example.demojavaspringboot;

import org.springframework.stereotype.Service;

@Service
public class MojeServise {

    public String getMojeMessage() {
        return "Tady Adam dělá dev";
    }
}