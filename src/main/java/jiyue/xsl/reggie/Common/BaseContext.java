package jiyue.xsl.reggie.Common;


/**
 * 基于theadlocal 封装的工具类，用户保存和获取的当前登录的用户id
 */
public class BaseContext {

    private static ThreadLocal<Long> shreadLocal = new ThreadLocal<>();

    public static void setShreadid(Long shread) {
        shreadLocal.set(shread);
    }


    public static Long getShreadid(){
        return shreadLocal.get();
    }

    public static ThreadLocal<Long> getShreadLocal() {
        return shreadLocal;
    }

    public static void setShreadLocal(ThreadLocal<Long> shreadLocal) {
        BaseContext.shreadLocal = shreadLocal;
    }
}
