package com.github.alien11689.messagenbrokers.jms.soj.impl;

import com.github.alien11689.messagenbrokers.jms.soj.api.Adder;
import com.github.alien11689.messagenbrokers.jms.soj.api.Result;
import com.github.alien11689.messagenbrokers.jms.soj.api.ToAdd;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public class AdderImpl implements Adder {
    @Override
    public Result add(@WebParam ToAdd toAdd) {
        Result result = new Result();
        result.setResult(toAdd.getA() + toAdd.getB());
        return result;
    }
}
