package com.person.demo;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ModuleException;
import com.alibaba.jvm.sandbox.api.ModuleLifecycle;
import com.alibaba.jvm.sandbox.api.http.Http;
import com.alibaba.jvm.sandbox.api.http.printer.ConcurrentLinkedQueuePrinter;
import com.alibaba.jvm.sandbox.api.http.printer.Printer;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleController;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.kohsuke.MetaInfServices;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Darren on 2018-06-13
 **/
@MetaInfServices(Module.class)
@Information(id = "drugWm", isActiveOnLoad = false, author = "abc@qq.com", version = "0.0.1")
public class DrugModule implements Module,ModuleLifecycle{

    @Resource
    private ModuleController moduleController;

    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    private Printer printer;

    @Http("/drugList")
    public void drugApi(final HttpServletResponse resp) throws ModuleException, IOException {
        moduleController.active();
        printer = new ConcurrentLinkedQueuePrinter(resp.getWriter());
        printer.println("SocketWatchman is working.\nPress CTRL_C abort it!");
        printer.waitingForBroken();
        moduleController.frozen();
    }


    @Override
    public void loadCompleted() {

        new EventWatchBuilder(moduleEventWatcher).
                onClass("com.acmed.his.api.DrugApi").onBehavior("getDrugList").
                onClass("com.acmed.his.api.DeptApi").onBehavior("getDeptList").
                onWatch(new AdviceListener(){
                    @Override
                    protected void before(Advice advice) throws Throwable {
                        if(advice.getBehavior().getName().equals("getDrugList")){
                            printer.println("before:getDrugList: ");
                            for(Object obj:advice.getParameterArray()){
                                printer.println(obj.toString());
                            }
                        }

                        if(advice.getBehavior().getName().equals("getDeptList")){
                            printer.println("before:getDeptList: ");
                            for(Object obj:advice.getParameterArray()){
                                printer.println(obj.toString());
                            }
                        }
                        super.before(advice);
                    }

                    @Override
                    protected void afterReturning(Advice advice) throws Throwable {
                        if(advice.getBehavior().getName().equals("getDrugList")){
                            printer.println("after:getDrugList： ");
                            printer.println(advice.getReturnObj().toString());

                        }
                        if(advice.getBehavior().getName().equals("getDeptList")){
                            printer.println("after:getDeptList: ");
                            printer.println(advice.getReturnObj().toString());
                        }
                        super.afterReturning(advice);
                    }
                });
    }

    @Override
    public void onLoad() throws Throwable {
    }

    @Override
    public void onUnload() throws Throwable {
    }

    @Override
    public void onActive() throws Throwable {
    }

    @Override
    public void onFrozen() throws Throwable {
    }



}
