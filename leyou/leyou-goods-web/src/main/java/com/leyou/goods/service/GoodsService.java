package com.leyou.goods.service;

import com.leyou.goods.client.BrandClient;
import com.leyou.goods.client.CategoryClient;
import com.leyou.goods.client.GoodsClient;
import com.leyou.goods.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    public Map<String,Object> loadData(Long spuId){
        Map<String,Object> model = new HashMap<>();
        //根据spuId查询spu
        final Spu spu = this.goodsClient.querySpuById(spuId);
        //查询spuDetail
        final SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spuId);
        //查询分类，Map<String,Object>
        final List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        final List<String> names = this.categoryClient.queryNamesByIds(cids);
        //初始化一个分类的Map
        List<Map<String,Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",cids.get(i));
            map.put("name",names.get(i));
            categories.add(map);
        }
        //查询品牌
        final Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //skus
        final List<Sku> skus = this.goodsClient.querySkuBySpuId(spuId);
        //查询规格参数组
        final List<SpecGroup> groups = this.specificationClient.queryGroupsWithParam(spu.getCid3());
        //查询特殊的规格参数
        final List<SpecParam> params = this.specificationClient.queryParams(null, spu.getCid3(), false, null);
        //初始化特殊规格参数的Map
        Map<Long,String> paramMap = new HashMap<>();
        params.forEach(specParam -> {
            paramMap.put(specParam.getId(),specParam.getName());
        });
        model.put("spu",spu);
        model.put("spuDetail",spuDetail);
        model.put("categories",categories);
        model.put("brand",brand);
        model.put("skus",skus);
        model.put("groups",groups);
        model.put("paramMap",paramMap);
        return model;
    }
}
