package com.github.alien11689.messagenbrokers.jms.requestreply.soj.api;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ToAdd {
    @XmlAttribute
    private int a;
    @XmlAttribute
    private int b;
}
