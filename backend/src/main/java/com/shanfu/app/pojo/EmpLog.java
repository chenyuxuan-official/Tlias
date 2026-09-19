package com.shanfu.app.pojo;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpLog {
    private Integer id; // ID
    private LocalDateTime operateTime; // 操作时间
    private String info; // 详细信息
}
