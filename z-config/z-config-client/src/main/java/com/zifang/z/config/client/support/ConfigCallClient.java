package com.zifang.z.config.client.support;

import com.zifang.util.core.meta.Result;
import com.zifang.util.core.meta.page.Pageable;
import com.zifang.util.http.base.define.*;
import com.zifang.z.config.common.model.ZConfigDTO;
import com.zifang.z.config.common.model.config.ZConfigPageRequest;
import com.zifang.z.config.common.model.config.ZConfigQueryRequest;
import com.zifang.z.config.common.model.config.ZConfigSaveRequest;


@RestController("http://${serverHost}:${serverPort}/config")
@RequestHeaders(
        @RequestHeader(key = "a",value = "xx")
)
public interface ConfigCallClient {

    @RequestMapping(value = "/getConfig", method = RequestMethod.POST)
    Result<String> getConfig(@RequestBody ZConfigQueryRequest request);

    @RequestMapping(value = "/saveConfig", method = RequestMethod.POST)
    Result<String> saveConfig(@RequestBody ZConfigSaveRequest request);

    @RequestMapping(value = "/pageConfig", method = RequestMethod.POST)
    Result<Pageable<ZConfigDTO>> pageConfig(@RequestBody ZConfigPageRequest request);
}
