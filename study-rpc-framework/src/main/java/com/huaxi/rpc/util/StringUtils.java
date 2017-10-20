package com.huaxi.rpc.util;

/**
 * Created by huwei on 2017/10/20.
 */
public class StringUtils {
  public static boolean isNotBlank(String str){
    if(str!=null&&str.length()>0)
      return true;
    return false;
  }
}
