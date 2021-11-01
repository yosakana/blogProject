package com.yxc.vo.params;

import lombok.Data;

// VO配置层操作对象
@Data
public class PageParams {

    //默认
    private int page = 1;

    //默认
    private int pageSize = 10;
}
