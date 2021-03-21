package com.worm.annotations;

@Table(name = "Table")
public class Tester {
    @Column(name = "col1")
    int anInt = 4;
    @Column(name = "col2")
    double aDouble = 1.5;
    @Column(name = "coL3")
    String aString = "Hello WORLD!!!!";
    String bString = "BYE World!";
}
