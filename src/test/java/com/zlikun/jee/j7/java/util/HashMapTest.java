package com.zlikun.jee.j7.java.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/6/7 9:45
 */
public class HashMapTest {

    @Test
    public void test() {

        HashMap<String ,String> map = new HashMap<>(3) ;        // 指定容量为3，实际会转换为4
        Assert.assertEquals(null ,map.put("name" ,"zlikun"));       // 第一次put，返回旧值(null)
        Assert.assertEquals("zlikun" ,map.put("name" ,"jinx"));     // 第二次put，返回旧值(zlikun)

        Assert.assertEquals("jinx" ,map.get("name"));               // get，返回新值(jinx)

        Assert.assertEquals(4 ,map._getTableLength());
        Assert.assertEquals(1 ,map._getModCount());
        Assert.assertEquals(0.75F ,map._getLoadFactor() ,0.00F);
        Assert.assertEquals(3 ,map._getThreshold());

        map.put("age" ,"120") ;
        map.put("email" ,"admin@zlikun.com") ;

        Assert.assertEquals(4 ,map._getTableLength());
        Assert.assertEquals(3 ,map._getModCount());
        Assert.assertEquals(0.75F ,map._getLoadFactor() ,0.00F);
        Assert.assertEquals(3 ,map._getThreshold());

        map.put("account" ,"zlikun") ;

        // 发生扩容，扩容为原容量2倍
        Assert.assertEquals(8 ,map._getTableLength());
        Assert.assertEquals(4 ,map._getModCount());
        Assert.assertEquals(0.75F ,map._getLoadFactor() ,0.00F);
        // 重新计算阈值
        Assert.assertEquals(6 ,map._getThreshold());
    }

}
