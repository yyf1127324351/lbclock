package com.lb.lbclock.task;

import com.lb.lbclock.service.MachineService;
import com.lb.lbclock.service.RedisTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ScheduledTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    RedisTemplateService redisTemplateService;
    @Autowired
    MachineService machineService;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void scheduled(){
        List<String> machineList =  redisTemplateService.getValues("attendanceMachine:");

        for (String str : machineList){
            //判断该序列号是否在缓存中，如果在，则说明在线，如果不在 则更新考勤机为离线
            String serialNumber = str.substring(0,str.indexOf("_"));
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("serialNumber",serialNumber);
            int machineStatus = Integer.valueOf(str.substring(str.length()-1,str.length()));

            if (machineStatus != 2){ //如果不是已删除的考勤机
                String redisSerialNumber = redisTemplateService.get(serialNumber);
                if (StringUtils.isNotBlank(redisSerialNumber)){
                    //更新考勤状态为 在线
                    if (machineStatus == 0){
                        paramMap.put("status",1);
                        updateAttendantMachine(paramMap);
                    }
                }else {
                    //更新考勤机状态为 离线
                    if (machineStatus == 1){
                        paramMap.put("status",0);
                        updateAttendantMachine(paramMap);
                    }

                }

            }

        }

        logger.info("定时刷新考勤机状态");
    }


    public void updateAttendantMachine(Map<String,Object> paramMap){
        machineService.updateAttendanceMachine(paramMap);
        redisTemplateService.set("attendanceMachine:"+paramMap.get("serialNumber"),paramMap.get("serialNumber")+"_"+paramMap.get("status"));
        logger.info("updateAttendanceMachineStatus-redisSerialNumber-success");
    }

}
