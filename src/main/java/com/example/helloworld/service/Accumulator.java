package com.example.helloworld.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class Accumulator implements Addable {
    private final AtomicInteger sum = new AtomicInteger();

    @Qualifier("multiAdder")
    @Autowired
    private Addable adder;

    @Override
    public int add(List<Integer> numbers) {
        return sum.addAndGet(adder.add(numbers));
    }
}
