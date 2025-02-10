package com.example.swaper.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaginatorService<T> {
    public Map<String, Object> paginate(List<T> baseList, String baseUrl, Integer page, long limit) {
        int size = baseList.size();
        int availablePage = (int)((size - 1) / limit) + 1;
        page = (page == null || page == 0) ? 1 : page;
        if(page > availablePage) {
            page = availablePage;
        }
        long offset = (page - 1) * limit;
        List<T> data = baseList.stream().skip(offset).limit(limit).toList();
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("seeMoreUrl", (page < availablePage) ? baseUrl+"?page="+(page+1) : null);
        return response;
    }
}
