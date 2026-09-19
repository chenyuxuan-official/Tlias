package com.shanfu.app.pojo;

import lombok.Data;
import java.util.List;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private Long total; // 总记录
    private List<T> rows; // 当前列表的页数
}
 // 泛型化，让分页结果能够同时装得下Clazz / Student