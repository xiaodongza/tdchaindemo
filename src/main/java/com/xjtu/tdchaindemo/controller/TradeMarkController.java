package com.xjtu.tdchaindemo.controller;

import cn.tdchain.Trans;
import cn.tdchain.jbcc.Result;
import com.xjtu.tdchaindemo.TDChainConnection;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mark")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class TradeMarkController extends BaseController {

    @PostMapping(value = "addTradeMark", consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public String addTradeMark(@RequestParam(name = "name") String name,
                               @RequestParam(name = "price") int price,
                               @RequestParam(name = "img") String img) {
        System.out.println(name);
        System.out.println(price);
        System.out.println(img.length());
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("price", price);
        data.put("img", img);
        try {

            TDChainConnection.storeMessageOnChain("xjtu_trademark", data, "trademark");
        } catch (Exception e) {
            return "fail";
        }
        return "success";
    }

    @PostMapping(value = "queryTradeMark", consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public List<String> queryAllTradeMark() {
        String type = "trademark";
        List<String> res = new ArrayList<>();
        try {
            Result<List<Trans>> result = TDChainConnection.connection.getTransListByType(type);
            System.out.println(result);
            List<Trans> trans = result.getEntity();
            for (Trans tran : trans) {
                res.add(tran.getData());
                System.out.println(tran.getData());
            }
        } catch (Exception e) {
            return null;
        }
        return res;
    }
}
