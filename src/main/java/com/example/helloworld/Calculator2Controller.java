package com.example.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Calculator2Controller {
    @Qualifier("accumulator")
    @Autowired
    private Addable accumulator;

    @Qualifier("multiAdder")
    @Autowired
    private Addable multiAdder;

    @RequestMapping("/calculator2/adder/{numbers}")
    public int adder(@PathVariable("numbers") List<Integer> numbers) {
        return multiAdder.add(numbers);
    }

    @RequestMapping("/calculator2/accumulator/{numbers}")
    public int accumulator(@PathVariable("numbers") List<Integer> numbers) {
        return accumulator.add(numbers);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleIllegalArgumentException(IllegalArgumentException iae) {
        return iae.getMessage();
    }
}