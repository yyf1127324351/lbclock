package com.lb.lbclock.config;

import com.lb.lbclock.model.Machine;
import com.lb.lbclock.model.Operator;
import com.lb.lbclock.service.MachineService;
import com.lb.lbclock.service.OperatorService;
import com.lb.lbclock.service.RedisTemplateService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SystemDataInitListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    RedisTemplateService redisTemplateService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private  MachineService machineService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        String redisKey = "attendanceNumber:";
        //删除所有相关redis中的数据
        redisTemplateService.batchDel(redisKey);

        //将所有员工id和考勤号码存到redis中。
        List<Operator> operatorList = operatorService.getAllOperator();
        for (Operator operator : operatorList){
            if (null != operator.getAttendanceNumber()){
                redisTemplateService.set(redisKey+operator.getAttendanceNumber().toString(),operator.getId().toString());
            }
        }

        //删除
        redisTemplateService.batchDel("attendanceMachine:");

        //初始化考勤机数据，缓存至redis
        List<Machine> machineList = machineService.getMachines();
        if (CollectionUtils.isNotEmpty(machineList)){
            for (Machine machine : machineList){
                redisTemplateService.set("attendanceMachine:"+machine.getSerialNumber(),machine.getSerialNumber()+"_"+machine.getStatus());
            }
        }

    }
}
