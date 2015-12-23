package com.u8.server.cache;

import com.u8.server.data.UChannel;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKScript;

import java.util.HashMap;
import java.util.Map;

/**
 * 每个渠道SDK都有一个实现了ISDKScript接口的SDK逻辑处理类，登录认证和获取订单号接口中，通过反射的方式来
 * 实例化对应渠道的类，可能会导致一定的性能瓶颈。所以，这里我们增加一个缓存，第一次实例化之后，将对应渠道的处理类，缓存起来。
 * 后面使用的时候，直接从缓存中取
 * Created by xiaohei on 15/12/23.
 */
public class SDKCacheManager {

    private static SDKCacheManager instance;

    private Map<Integer, ISDKScript> sdkCaches;

    private SDKCacheManager(){
        sdkCaches = new HashMap<Integer, ISDKScript>();
    }

    public static SDKCacheManager getInstance(){
        if(instance == null){
            instance = new SDKCacheManager();

        }

        return instance;
    }

    /***
     * 获取指定渠道的ISDKScript的实例
     * @param channel
     * @return
     */
    public ISDKScript getSDKScript(UChannel channel){

        if(channel == null){
            return  null;
        }

        if(sdkCaches.containsKey(channel.getChannelID())){
            return sdkCaches.get(channel.getChannelID());
        }

        try {
            Log.d("now to init a new intance of channel:"+channel.getChannelID());
            ISDKScript script = (ISDKScript)Class.forName(channel.getMaster().getVerifyClass()).newInstance();
            sdkCaches.put(channel.getChannelID(), script);
            return script;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
