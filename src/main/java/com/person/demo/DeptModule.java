package com.person.demo;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ModuleException;
import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.filter.NameRegexFilter;
import com.alibaba.jvm.sandbox.api.http.Http;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.api.resource.ModuleController;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.kohsuke.MetaInfServices;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Darren on 2018-06-15
 **/
@MetaInfServices(Module.class)
@Information(id = "deptWm", author = "test@qq.com", version = "0.0.1")
public class DeptModule implements Module{
    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    @Http("/deptList")
    public void deptApi(final HttpServletResponse resp) throws ModuleException, IOException {

        moduleEventWatcher.watch(
            // 匹配到DeptApi#getDeptList()
            new NameRegexFilter("com.acmed.his.service.DeptManager","getDeptVoList"),
            new EventListener(){
                @Override
                public void onEvent(Event event) throws Throwable {
                    Thread.sleep(1000);

                    //立即返回，因为监听的是BEFORE事件，所以此时立即返回，方法体将不会被执行；
                    ProcessControlException.throwReturnImmediately(null);
                }
            }, Event.Type.BEFORE);
    }

}
