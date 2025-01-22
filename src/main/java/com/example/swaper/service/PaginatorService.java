package com.example.swaper.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaginatorService<T> {
    public List<T> paginate(List<T> baseList, Integer page, long limit) {
        long offset = (page - 1) * limit;
        return baseList.stream().skip(offset).limit(limit).toList();
    }
}
