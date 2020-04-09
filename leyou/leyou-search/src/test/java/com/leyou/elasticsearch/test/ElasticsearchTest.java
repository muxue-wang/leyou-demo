package com.leyou.elasticsearch.test;

import com.leyou.LeyouSearchApplication;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.search.repositery.GoodsRepository;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = LeyouSearchApplication.class)
@RunWith(SpringRunner.class)
public class ElasticsearchTest {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsClient goodsClient;
    @Test
    public void test(){
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);

        Integer page = 1;
        Integer rows = 100;
        do {
            //分页查询spu，获取分页结果集
            final PageResult<SpuBo> result = goodsClient.querySpuByPage(null, null, page, rows);
            //获取当前页的数据
            final List<SpuBo> items = result.getItems();
            //处理List<SpoBo> => List<Goods>
            final List<Goods> goodsList = items.stream().map(spuBo -> {
                try {
                    return searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            //执行新增数据
            goodsRepository.saveAll(goodsList);
            rows = items.size();
            page++;
        }while (rows==100);

    }
}
