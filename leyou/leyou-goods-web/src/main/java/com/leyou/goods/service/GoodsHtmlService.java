package com.leyou.goods.service;

import com.leyou.goods.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine engine;

    @Autowired
    private GoodsService goodsService;

    public void createHtml(Long spuId){
        //初始化运行上下文
        Context context = new Context();
        //设置数据模型
        context.setVariables(this.goodsService.loadData(spuId));

        PrintWriter printWriter = null;
        try {
            File file = new File("E:\\leyou\\utils\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);
            this.engine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (printWriter != null){
                printWriter.close();
            }
        }
    }

    public void deleteHtml(Long spuId){
        File file = new File("E:\\leyou\\utils\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
        file.deleteOnExit();
    }

    /**
     * 线程池优化
     * @param spuId
     */
    public void asyncExecute(Long spuId){
        ThreadUtils.execute(()->createHtml(spuId));
    }
}
