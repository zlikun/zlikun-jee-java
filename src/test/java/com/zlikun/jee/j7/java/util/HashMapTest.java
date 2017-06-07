package com.zlikun.jee.j7.java.util;

import org.junit.Assert;
import org.junit.Ignore;
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

    /**
     * 测试HashMap扩容能性能的影响
     */
    @Test @Ignore
    public void resize_performance() {

        int power = 12 ;
        int loop = 100 * 1000 ;

        // 两次扩容测试
        long time = System.currentTimeMillis() ;

        for (int i = 0; i < loop; i++) {
            twice_resize(power);
        }

        // 2789 / 2759 / 2865
        System.out.printf("程序执行耗时：%d 毫秒!\n" ,System.currentTimeMillis() - time);

        // 无扩容测试
        time = System.currentTimeMillis() ;

        for (int i = 0; i < loop; i++) {
            no_resize(power);
        }

        // 2466 / 2511 / 2539
        System.out.printf("程序执行耗时：%d 毫秒!\n" ,System.currentTimeMillis() - time);

    }

    /**
     * 无扩容测试
     */
    private void no_resize(int power) {
        // 初始容量 1 << 12 ，表示2的12次幂，等价于 (int) Math.pow(2 ,12)，值为：4096
        HashMap<String ,String> map = new HashMap<>(1 << power) ;

        // 结束时插入未超过阈值，不触发扩容
        int length = 1 << power * 3 / 4 ;
        for (int i = 0; i < length; i++) {
            map.put("key-" + i ,"value-" + i) ;
        }
    }

    /**
     * 两次扩容测试
     */
    private void twice_resize(int power) {

        HashMap<String ,String> map = new HashMap<>(1 << (power - 2)) ;

        // 第一次扩容，扩容为 1 << (power - 1)
        // 第二次扩容，扩容为 1 << power
        // 结束时插入未超过阈值，不触发扩容
        int length = 1 << power * 3 / 4 ;
        for (int i = 0; i < length; i++) {
            map.put("key-" + i ,"value-" + i) ;
        }
    }

}
