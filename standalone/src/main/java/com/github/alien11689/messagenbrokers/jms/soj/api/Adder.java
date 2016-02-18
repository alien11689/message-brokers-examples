package com.github.alien11689.messagenbrokers.jms.soj.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface Adder {
    @WebMethod
    @WebResult
    Result add(@WebParam ToAdd toAdd);
}