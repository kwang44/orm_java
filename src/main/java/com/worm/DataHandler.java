package com.worm;

import com.worm.annotations.Column;
import com.worm.annotations.Table;

import java.lang.annotation.Annotation;

@Table(name = "Hello")
public class DataHandler {

    Integer i = 1;
    Double d = 1.5;


    public static void main(String[] args) {

        DataHandler dataHandler = new DataHandler();

    }
}
