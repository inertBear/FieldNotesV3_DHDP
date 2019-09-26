package com.devhunter.dhdp.infrastructure;

public class DHDummyService extends DHService {

    public DHDummyService(String name){
        this.mName = name;
    }

    public String tellMeSomething() {
        return "Something, Something, Dark Side";
    }
}
