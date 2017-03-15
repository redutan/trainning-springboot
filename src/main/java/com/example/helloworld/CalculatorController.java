package com.example.helloworld;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CalculatorController {
    @RequestMapping("/calculator/adder/{numbers}")
    public Integer adder(@PathVariable("numbers") List<Integer> numbers) {
        // FIXME checkParameter()
        if (hasDuplicated(numbers)) {
            throw new IllegalArgumentException("오류 : 같은 수는 입력할 수 없습니다.");
        }
        return numbers.stream().reduce(0, Integer::sum);
    }

    private void checkParameter(List<Integer> numbers) {
        if (hasDuplicated(numbers)) {
            throw new IllegalArgumentException("오류 : 같은 수는 입력할 수 없습니다.");
        }
    }

    private boolean hasDuplicated(List<Integer> numbers) {
        long size = numbers.size();
        long distinctSize = numbers.stream().distinct().count();
        return size != distinctSize;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleIllegalArgumentException(IllegalArgumentException iae) {
        return iae.getMessage();
    }
}