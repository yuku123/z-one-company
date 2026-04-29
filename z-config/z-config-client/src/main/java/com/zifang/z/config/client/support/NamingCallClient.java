package com.zifang.z.config.client.support;

import com.zifang.util.core.meta.Result;
import com.zifang.util.http.base.define.*;
import com.zifang.z.config.common.model.ZNamingInstance;
import com.zifang.z.config.common.model.naming.ZNamingInstanceDeregisterRequest;
import com.zifang.z.config.common.model.naming.ZNamingInstanceRegisterRequest;
import com.zifang.z.config.common.model.naming.ZNamingSubscribeRequest;
import com.zifang.z.config.common.model.naming.ZNamingUnsubscribeRequest;

import java.util.List;

@RestController("http://${serverHost}:${serverPort}/naming")
public interface NamingCallClient {

    // ===================== 服务注册接口 =====================
    @RequestMapping(value = "/registerInstance", method = RequestMethod.POST)
    Result<String> registerInstance(@RequestBody ZNamingInstanceRegisterRequest request);

    @RequestMapping(value = "/registerInstance/simple", method = RequestMethod.POST)
    Result<String> registerInstanceSimple(
            @RequestParam String serviceName,
            @RequestParam String ip,
            @RequestParam Integer port);

    @RequestMapping(value = "/registerInstance/withCluster", method = RequestMethod.POST)
    Result<String> registerInstanceWithCluster(
            @RequestParam String serviceName,
            @RequestParam String ip,
            @RequestParam Integer port,
            @RequestParam String clusterName);

    // ===================== 服务注销接口 =====================
    @RequestMapping(value = "/deregisterInstance", method = RequestMethod.DELETE)
    Result<String> deregisterInstance(@RequestBody ZNamingInstanceDeregisterRequest request);

    @RequestMapping(value = "/deregisterInstance/simple", method = RequestMethod.DELETE)
    Result<String> deregisterInstanceSimple(
            @RequestParam String serviceName,
            @RequestParam String ip,
            @RequestParam Integer port);

    @RequestMapping(value = "/deregisterInstance/withCluster", method = RequestMethod.DELETE)
    Result<String> deregisterInstanceWithCluster(
            @RequestParam String serviceName,
            @RequestParam String ip,
            @RequestParam Integer port,
            @RequestParam String clusterName);

    // ===================== 服务查询接口 =====================
    @RequestMapping(value = "/getAllInstances", method = RequestMethod.GET)
    Result<List<ZNamingInstance>> getAllInstances(
            @RequestParam String serviceName,
            @RequestParam(required = false, defaultValue = "DEFAULT_GROUP") String group,
            @RequestParam(required = false, defaultValue = "") String namespace);

    @RequestMapping(value = "/selectInstances/healthy", method = RequestMethod.GET)
    Result<List<ZNamingInstance>> selectInstances(
            @RequestParam String serviceName,
            @RequestParam boolean healthy,
            @RequestParam(required = false) String clusterName);

    @RequestMapping(value = "/selectOneHealthyInstance", method = RequestMethod.GET)
    Result<ZNamingInstance> selectOneHealthyInstance(@RequestParam String serviceName);

    // ===================== 服务订阅/取消订阅接口 =====================
    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    Result<String> subscribe(@RequestBody ZNamingSubscribeRequest request);

    @RequestMapping(value = "/unsubscribe", method = RequestMethod.POST)
    Result<String> unsubscribe(@RequestBody ZNamingUnsubscribeRequest request);
}
