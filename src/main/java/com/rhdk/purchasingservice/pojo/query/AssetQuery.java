package com.rhdk.purchasingservice.pojo.query;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssetQuery {
    List<Long> assetIds;
    Long assetTemplId;
}
