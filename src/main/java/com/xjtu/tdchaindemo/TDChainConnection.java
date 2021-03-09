package com.xjtu.tdchaindemo;

import cn.tdchain.Trans;
import cn.tdchain.TransHead;
import cn.tdchain.cipher.Cipher;
import cn.tdchain.jbcc.Connection;
import cn.tdchain.jbcc.ConnectionFactory;
import cn.tdchain.jbcc.Result;
import cn.tdchain.jbcc.bql.BQL;
import cn.tdchain.jbcc.bql.BQLResult;
import cn.tdchain.jbcc.bql.Condition;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TDChainConnection {

    protected static String[] iptables = new String[]{
            "open-tdcb-node1.tdchain.cn",
            "open-tdcb-node2.tdchain.cn",
            "open-tdcb-node3.tdchain.cn",
            "open-tdcb-node4.tdchain.cn"};

    protected final static String keystorePath = "src/main/resources/0x66ab53d956cc3c274456ca59332836d3.pfx";
    protected final static String keystorePasswd = "beyond3339280";

    //# Access to Tiandeyun block chain port, default is18088
    protected final static int port = 18088;
    //# Connection timeout, default 3 seconds
    protected final static long timeout = 3000;
    //# Visit token of Tiandeyun Block Chain and obtain valid certificate after successful application token
    protected final static String token = "32581b83-705d-40ef-9f62-1f9a4118fbe1";
//# Authorization files needed to access Tiandeyun block chain can be downloaded after successful login, and keystore Path is the certificate storage path.

    //# Set SM or RSA cipher
    static Cipher.Type type = Cipher.Type.RSA;//or Cipher.Type.SM

    //# Declare a global connector
    public static Connection connection = null;

    static {
        try {
            //# Building configuration information
            ConnectionFactory factory = ConnectionFactory.ConnectionConfig.builder()
                    .cipherType(type)
                    .iptables(iptables)
                    .port(port)  //# No configuration even with default value 18088
                    .timeout(timeout)  //# Use default 3 seconds without configuration
                    .token(token)
                    //.showPrint()
                    .keystorePath(keystorePath)
                    .keystorePassword(keystorePasswd).build();

            connection = factory.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void storeMessageOnChain() {
        //# Construct a transaction information
        Trans trans = new Trans();
        trans.setKey("warne");//# Key is the dimension of current transactions
        Map<String, Object> data = new HashMap<>();
        data.put("name", "warne");
        data.put("age", 20);
        data.put("where", "I am tian de technology.");
        trans.setData(JSON.toJSONString(data));
        trans.setType("user_info");
        trans.setTimestamp(new Date().getTime());

        //# Initiate a transaction to cloud block chain services
        Result<TransHead> result = connection.addTrans(trans);
        if (result.isSuccess()) {
            //# Determine success based on the status of returned results
            System.out.println("\n===> add trans success.");
        } else {
            System.out.println("\n===> add trans fail.");
        }
    }

    public static void main(String[] args) {
        //第一个条件：姓名等于=xiaoming  的
        Condition c1 = new Condition("name", BQL.Relationship.equal, "xiaoming");

        //第二个条件：年龄大于18岁 的
        Condition c2 = new Condition("age", BQL.Relationship.greater, 18);

        //条件c1和c2是并且关系，一次查询可同时支持10个条件对象Condition
        c1.setAnd(c2);//相当于: name=="xiaoming" && age > 18

        //BQL区块链面向对象查询条件，根据k、v条件查询交易列表。
        BQL bql = new BQL();
        bql.setPage(1);//默认查询第一页
        bql.setCondition(c1);

        try {
            Result result = connection.getNewTransByBQL(bql);

            if (result.isSuccess()) {
                BQLResult bqlResult = (BQLResult) result.getEntity();
                System.out.println("bqlResult page=" + bqlResult.getPage());//page int 获取本次返回的页码坐标，页码坐标最小是：1 （俗称第一页）
                System.out.println("bqlResult size=" + bqlResult.getSize());//size int 本次查询总条数
                System.out.println("bqlResult count=" + bqlResult.getCount());//count int 本次实际返回条数，一次查询返回不会超过30条，否则系统会自动分页。
                System.out.println("bqlResult getList().size()=" + bqlResult.getList().size());
                for (Trans t : bqlResult.getList()) {
                    System.out.println(t.toJsonString());
                }
                System.out.println("\n===> query new trans success.");
            } else {
                System.out.println("\n===> query new trans fail.");
            }
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
