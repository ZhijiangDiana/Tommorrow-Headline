package com.heima.model.wemedia.vos;

import com.heima.model.wemedia.pojos.WmNews;
import lombok.Data;

@Data
public class WmNewsAuthVO extends WmNews {
    private String authorName;
}
