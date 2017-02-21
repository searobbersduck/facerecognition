package org.beast.voice;

/**
 * Created by weidong.zhang on 2016/5/17.
 */
public class Person {
    public int _id;
    public String name;
    public int age;
    public String info;

    public Person() {
    }

    public Person(String name, int age, String info) {
        this.name = name;
        this.age = age;
        this.info = info;
    }
}
