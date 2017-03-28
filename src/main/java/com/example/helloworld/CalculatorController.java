package com.example.helloworld;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class CalculatorController {
    private final AtomicInteger sum = new AtomicInteger();

    @RequestMapping("/calculator/adder/{numbers}")
    public ResponseEntity<Object> adder(@PathVariable("numbers") List<Integer> numbers) {
        if (hasDuplicated(numbers)) {
            return ResponseEntity.badRequest().body("오류 : 같은 수는 입력할 수 없습니다.");
        }
        return ResponseEntity.ok(sum(numbers));
    }

    private boolean hasDuplicated(List<Integer> numbers) {
        long size = numbers.size();
        long distinctSize = numbers.stream().distinct().count();
        return size != distinctSize;
    }

    private int sum(Collection<Integer> numbers) {
        int result = 0;
        for (Integer number : numbers) {
            result += number;
        }
        return result;
//        return numbers.stream().reduce(0, Integer::sum);
//        return numbers.stream().mapToInt(Integer::intValue).sum();
    }

    @RequestMapping("/calculator/adder2/{numbers}")
    public int adder2(@PathVariable("numbers") List<Integer> numbers) {
        checkParameter(numbers);
        return sum(numbers);
    }

    private void checkParameter(List<Integer> numbers) {
        if (hasDuplicated(numbers)) {
            throw new IllegalArgumentException("오류 : 같은 수는 입력할 수 없습니다.");
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleIllegalArgumentException(IllegalArgumentException iae) {
        return iae.getMessage();
    }

    @RequestMapping("/calculator/adder3/{numbers}")
    public int adder3(@PathVariable("numbers") Set<Integer> numbers) {
        return sum(numbers);
    }

    @RequestMapping("/calculator/accumulator/{numbers}")
    public int accumulator(@PathVariable("numbers") List<Integer> numbers) {
        return sum.accumulateAndGet(sum(numbers), Integer::sum);
    }
}