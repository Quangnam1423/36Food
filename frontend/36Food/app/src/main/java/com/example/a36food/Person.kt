package com.example.a36food

class Person {
    private var name : String
    private var age : Int
    public fun hello() {
        print("hello " + this.name)
    }

    constructor(name : String, age : Int) {
        this.name = name
        this.age = age
    }
}