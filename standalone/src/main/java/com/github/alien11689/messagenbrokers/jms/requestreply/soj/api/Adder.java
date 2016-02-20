package com.github.alien11689.messagenbrokers.jms.requestreply.soj.api;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface Adder {
    @WebResult(name = "result")
    Result add(@WebParam(name = "toAdd") ToAdd toAdd);
}