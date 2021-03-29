package com.korm.test;

import com.korm.annotations.Column;
import com.korm.annotations.Table;

import java.util.Objects;

@Table(name = "Korm Test Table")
public class TestObject {
    @Column(name = "Column 1", isPrimaryKey = true)
    int col1;
    @Column(name = "Column 2", isPrimaryKey = true)
    double col2;
    @Column(name = "Column 3", isPrimaryKey = true)
    String col3;
    @Column(name = "Column 4")
    int col4;
    @Column(name = "Column 5")
    double col5;
    @Column(name = "Column 6")
    String col6;

    public TestObject(){}

    public TestObject(int i) {
        this.col1 = i;
        this.col2 = i;
        this.col3 = Integer.toString(i);
        this.col4 = i;
        this.col5 = i;
        this.col6 = Integer.toString(i);
    }

    public boolean equals(TestObject testObject) {
        if (this.col1 == testObject.col1
                && this.col2 == testObject.col2
                && this.col3.equals(testObject.col3)
                && this.col4 == testObject.col4
                && this.col5 == testObject.col5
                && this.col6.equals(testObject.col6)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "col1=" + col1 +
                ", col2=" + col2 +
                ", col3='" + col3 + '\'' +
                ", col4=" + col4 +
                ", col5=" + col5 +
                ", col6='" + col6 + '\'' +
                '}';
    }
}
