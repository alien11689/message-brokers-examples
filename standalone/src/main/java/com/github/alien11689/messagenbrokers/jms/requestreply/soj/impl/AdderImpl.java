package com.github.alien11689.messagenbrokers.jms.requestreply.soj.impl;

import com.github.alien11689.messagenbrokers.jms.requestreply.soj.api.Adder;
import com.github.alien11689.messagenbrokers.jms.requestreply.soj.api.Result;
import com.github.alien11689.messagenbrokers.jms.requestreply.soj.api.ToAdd;

public class AdderImpl implements Adder {
    @Override
    public Result add(ToAdd toAdd) {
        Result result = new Result();
        result.setResult(toAdd.getA() + toAdd.getB());
        return result;
    }
}
