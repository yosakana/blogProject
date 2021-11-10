package com.yxc.vo.params;

import lombok.Data;

// VO配置层操作对象
@Data
public class PageParams {

    //默认
    private int page = 1;

    //默认
    private int pageSize = 10;

    //类别id
    private Long categoryId;

    //标签id
    private Long tagId;

    //年月，用于文章归档模块
    private String year;
    private String month;

    //如果是个位数的月，就要加个0，变成 0X 月
    public String getMoneth(){
        if(this.month != null && this.month.length() == 1){
            return 0+this.month;
        }

        return this.month;
    }
}
