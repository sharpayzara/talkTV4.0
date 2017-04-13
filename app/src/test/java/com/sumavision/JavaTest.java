package com.sumavision;

import android.util.Log;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by sharpay on 16-10-18.
 */
public class JavaTest {
    @Test
    public void testAdd() {

        int i = 0;
        i = 4+4;
        System.out.print(".............. "+i);
        // 比较 i 是否 等于 8 ，相等的话通过测试！！！
        Assert.assertEquals(8, i);
    }
}
