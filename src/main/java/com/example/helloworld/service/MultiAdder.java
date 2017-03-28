package com.example.helloworld.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MultiAdder implements Addable {
    @Override
    public int add(List<Integer> numbers) {
        checkParameter(numbers);
        return sum(numbers);
    }

    private void checkParameter(List<Integer> numbers) {
        if (numbers.size() < 2) {
            throw new IllegalArgumentException("오류 : 숫자는 최소 2개 이상 입력해야합니다.");
        }
        if (hasDuplicated(numbers)) {
            throw new IllegalArgumentException("오류 : 중복되는 수가 존재합니다.");
        }
    }

    private boolean hasDuplicated(List<Integer> numbers) {
        long size = numbers.size();
        long distinctSize = numbers.stream().distinct().count();
        return size != distinctSize;
    }

    private int sum(List<Integer> numbers) {
        int result = 0;
        for (Integer number : numbers) {
            result += number;
        }
        return result;
    }
}
