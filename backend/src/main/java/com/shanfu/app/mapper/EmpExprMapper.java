package com.shanfu.app.mapper;

import com.shanfu.app.pojo.EmpExpr;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmpExprMapper  {
    

    // 批量新增员工工作经历信息
    public void insertBatch(List<EmpExpr> exprList);

    public void deleteByEmpIds(List<Integer> empIds);
}
