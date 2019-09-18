package com.lb.lbclock.controller.attendance;

import com.lb.lbclock.controller.BaseController;
import com.lb.lbclock.service.ClockService;
import com.lb.lbclock.service.RedisTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
* 连接考勤机-打卡数据传输controller
* */

@RestController
@RequestMapping("/iclock")
public class ClockController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ClockController.class);

    @Autowired
    private ClockService clockService;

    @Autowired
    RedisTemplateService redisTemplateService;

    @RequestMapping("/cdata")
    public void response(HttpServletRequest request, HttpServletResponse response)throws Exception {
        Map<String, Object> ret = getParametersMap(request);
        logger.info("parameter: {}", ret);

        String method = request.getMethod();
        if (method.equals("GET")){
            String SN = request.getParameter("SN").toString();//设备号

            //将考勤机序列号缓存进redis,失效时间是150秒
            //用于考勤机在线离线判断。
            redisTemplateService.set(SN,SN,150);

            response.getWriter().print("GET OPTION FROM:" +" " +SN + "\n");
            response.getWriter().print("ATTLOGStamp=None" + "\n");
            response.getWriter().print("ErrorDelay=300" + "\n");
            response.getWriter().print("Delay=60"+ "\n");
            response.getWriter().print("TransTimes=12:00;14:00;23:45" + "\n"); //指定时间传输
            response.getWriter().print("TransInterval=1" + "\n");// 间隔时间传输，时长单位（分钟）
            response.getWriter().print("TransFlag=1000000000" + "\n");
            response.getWriter().print("TimeZone=8" + "\n");
            response.getWriter().print("Realtime=1"+ "\n"); //设置是否实时传输。1：是 0：不是  如果实时传输，则TransTimes，TransInterval失效
            response.getWriter().print("Encrypt=None" + "\n");
            response.getWriter().flush();
            response.getWriter().close();
        }else {

            //将考勤机的打卡记录实时同步进系统
            String result = clockService.addClockRecord(request);
            if ("success".equals(result)){
                response.getWriter().print("OK:" + 9 + "\n");
            }
            response.getWriter().flush();
            response.getWriter().close();

        }



    }
}
