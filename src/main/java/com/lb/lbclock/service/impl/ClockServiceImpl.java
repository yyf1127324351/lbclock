package com.lb.lbclock.service.impl;

import com.lb.lbclock.common.Constant;
import com.lb.lbclock.dao.ClockMapper;
import com.lb.lbclock.dao.MachineMapper;
import com.lb.lbclock.model.Clock;
import com.lb.lbclock.service.ClockService;
import com.lb.lbclock.service.RedisTemplateService;
import com.lb.lbclock.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class ClockServiceImpl implements ClockService{

    private static final Logger logger = LoggerFactory.getLogger(ClockServiceImpl.class);
    @Autowired
    RedisTemplateService redisTemplateService;

    @Autowired
    private ClockMapper clockMapper;
    @Autowired
    MachineMapper machineMapper;

    @Override
    public String addClockRecord(HttpServletRequest request) {

        try {
            String sn = request.getParameter("SN").toString();//设备号
            InputStream inputStream =  request.getInputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String str = new String(bytes,"GB2312");
            String formatString = str.replace("\t",",");
            List<String> recordList = Arrays.asList(formatString.split("\n"));
            if (CollectionUtils.isNotEmpty(recordList)){
                for(String record : recordList){
                    String attendanceNumber = record.substring(0,record.indexOf(","));
                    String clockTime = record.substring(record.indexOf(",") + 1,record.indexOf(",", record.indexOf(",") + 1));
                    Clock clock = new Clock();
                    clock.setAttendanceNumber(Long.valueOf(attendanceNumber));
                    clock.setClockInTime(clockTime);
                    //打卡时间
                    Date date = DateUtils.stringToDate1(clockTime);
                    String dataStr = DateUtils.dateToString3(date);
                    clock.setClockDateTime(dataStr);


                    clock.setSerialNumber(sn);
                    clock.setSource(1);
                    clock.setCreateTime(new Date());
                    clock.setUpdateTime(new Date());
                    clock.setSourceDescribe("考勤机序列号-"+sn);
                    clock.setClockPlace(Constant.machineMap.get(sn));


                    if (null != redisTemplateService.get("attendanceNumber:"+attendanceNumber)){
                        Integer operatorId = Integer.valueOf(redisTemplateService.get("attendanceNumber:"+attendanceNumber));
                        if (null != operatorId){
                            clock.setOperatorId(operatorId.intValue());
                        }
                    }

                    //验证一分钟内是否有打卡记录，如果有，则更新。
                    String dataString = redisTemplateService.get("clockTime:"+attendanceNumber);
                    if (StringUtils.isNotBlank(dataString) && dataString.equals(dataStr)){
                        //更新操作
                        clockMapper.updateClockRecord(clock);
                        redisTemplateService.set("clockTime:"+attendanceNumber,dataString,300);
                    }else {
                        clockMapper.addClockRecord(clock);
                        redisTemplateService.set("clockTime:"+attendanceNumber,dataStr,300);
                    }

                    Map<String,Object> paramMap = new HashMap<>();
                    paramMap.put("serialNumber",sn);
                    paramMap.put("lastReceiveTime",clockTime);
                    paramMap.put("updateTime",new Date());
                    machineMapper.updateAttendanceMachine(paramMap);
                }

            }
            logger.info("record: {}", str);
        } catch (IOException e) {
            logger.error("addClockRecord-exception:{}",e);
            return "false";
        }
        return "success";
    }
}
