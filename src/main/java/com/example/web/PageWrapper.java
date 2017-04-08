package com.example.web;

import lombok.Getter;
import lombok.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;

public class PageWrapper<T> implements Page<T> {
    public static final int MAX_PAGE_ITEM_DISPLAY = 10;
    private Page<T> page;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Getter
    private List<PageItem> pageItems;
    @Getter
    private int currentNumber;
    @Getter
    private String url;

    public PageWrapper(Page<T> page, HttpServletRequest request) {
        String url = request.getRequestURI();
        String queryString = toQueryStringWithoutPage(request);
        commonConstructor(page, url + queryString);
    }

    // ?param1=value1&param2=value2 로 변환
    private String toQueryStringWithoutPage(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        return Collections.list(parameterNames).stream()
                .filter(paramName -> !"page".equals(paramName))
                .map(paramName -> toRequestParamString(request, paramName))
                .collect(joining("&", "?", ""));
    }

    // "name=value" 로 반환
    private String toRequestParamString(HttpServletRequest request, String paramName) {
        String paramValue = request.getParameter(paramName);
        try {
            return paramName + "=" + URLEncoder.encode(paramValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void commonConstructor(Page<T> page, String url) {
        this.page = page;
        this.url = url;
        this.currentNumber = page.getNumber(); //start from 0 to match page.page

        int start, size;
        if (page.getTotalPages() <= MAX_PAGE_ITEM_DISPLAY) {
            start = 0;
            size = page.getTotalPages();
        } else {
            if (currentNumber <= MAX_PAGE_ITEM_DISPLAY - (MAX_PAGE_ITEM_DISPLAY / 2)) {
                start = 0;
                size = MAX_PAGE_ITEM_DISPLAY;
            } else if (currentNumber >= page.getTotalPages() - (MAX_PAGE_ITEM_DISPLAY / 2)) {
                start = page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY;
                size = MAX_PAGE_ITEM_DISPLAY;
            } else {
                start = currentNumber - (MAX_PAGE_ITEM_DISPLAY / 2);
                size = MAX_PAGE_ITEM_DISPLAY;
            }
        }
        this.pageItems = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.pageItems.add(new PageItem(start + i, (start + i) == currentNumber));
        }
    }

    public int getNumber() {
        return currentNumber;
    }

    @Override
    public List<T> getContent() {
        return page.getContent();
    }

    @Override
    public boolean hasContent() {
        return page.hasContent();
    }

    @Override
    public Sort getSort() {
        return page.getSort();
    }

    @Override
    public int getSize() {
        return page.getSize();
    }

    @Override
    public int getNumberOfElements() {
        return page.getNumberOfElements();
    }

    @Override
    public int getTotalPages() {
        return page.getTotalPages();
    }

    @Override
    public long getTotalElements() {
        return page.getTotalElements();
    }

    @Override
    public <S> Page<S> map(Converter<? super T, ? extends S> converter) {
        return page.map(converter);
    }

    @Override
    public boolean isFirst() {
        return page.isFirst();
    }

    @Override
    public boolean isLast() {
        return page.isLast();
    }

    @Override
    public boolean hasNext() {
        return page.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return page.hasPrevious();
    }

    @Override
    public Pageable nextPageable() {
        return page.nextPageable();
    }

    @Override
    public Pageable previousPageable() {
        return page.previousPageable();
    }

    @Override
    public Iterator<T> iterator() {
        return page.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        page.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return page.spliterator();
    }

    @Value
    public static class PageItem {
        private int number;
        private boolean current;
    }
}
