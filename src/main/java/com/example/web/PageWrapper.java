package com.example.web;

import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class PageWrapper<T> {
    public static final int MAX_PAGE_ITEM_DISPLAY = 10;
    private Page<T> page;
    private List<PageItem> pageItems;
    private int currentNumber;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PageWrapper(Page<T> page, String url) {
        commonConstructor(page, url);
    }

    public PageWrapper(Page<T> page, HttpServletRequest request) {
        String url = request.getRequestURI();
        Enumeration<String> parameterNames = request.getParameterNames();
        String queryString = Collections.list(parameterNames).stream()
                .filter(paramName -> !"page".equals(paramName))
                .filter(paramName -> !"size".equals(paramName))
                .filter(paramName -> !"sort".equals(paramName))
                .map(paramName -> toRequestParamString(request, paramName))
                .collect(joining("&", "?", ""));
        commonConstructor(page, url + queryString);
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
        currentNumber = page.getNumber(); //start from 0 to match page.page

        int start, size;
        if (page.getTotalPages() <= MAX_PAGE_ITEM_DISPLAY) {
            start = 0;
            size = page.getTotalPages();
        } else {
            if (currentNumber <= MAX_PAGE_ITEM_DISPLAY - MAX_PAGE_ITEM_DISPLAY / 2) {
                start = 0;
                size = MAX_PAGE_ITEM_DISPLAY;
            } else if (currentNumber >= page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY / 2) {
                start = page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY;
                size = MAX_PAGE_ITEM_DISPLAY;
            } else {
                start = currentNumber - MAX_PAGE_ITEM_DISPLAY / 2;
                size = MAX_PAGE_ITEM_DISPLAY;
            }
        }
        pageItems = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            pageItems.add(new PageItem(start + i, (start + i) == currentNumber));
        }
    }

    public List<PageItem> getPageItems() {
        return pageItems;
    }

    public int getNumber() {
        return currentNumber;
    }

    public List<T> getContent() {
        return page.getContent();
    }

    public int getSize() {
        return page.getSize();
    }

    public int getTotalPages() {
        return page.getTotalPages();
    }

    public boolean isFirst() {
        return page.isFirst();
    }

    public boolean isLast() {
        return page.isLast();
    }

    public boolean isHasPrevious() {
        return page.hasPrevious();
    }

    public boolean isHasNext() {
        return page.hasNext();
    }

    public boolean isEmpty() {
        return page.getTotalElements() == 0;
    }

    public class PageItem {
        private int number;
        private boolean current;

        public PageItem(int number, boolean current) {
            this.number = number;
            this.current = current;
        }

        public int getNumber() {
            return this.number;
        }

        public boolean isCurrent() {
            return this.current;
        }
    }
}
