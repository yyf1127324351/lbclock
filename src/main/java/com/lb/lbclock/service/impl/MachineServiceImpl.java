package com.lb.lbclock.service.impl;

import com.lb.lbclock.dao.MachineMapper;
import com.lb.lbclock.model.Machine;
import com.lb.lbclock.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MachineServiceImpl implements MachineService {
    @Autowired
    MachineMapper machineMapper;
    @Override
    public void updateAttendanceMachine(Map<String, Object> paramMap) {
        machineMapper.updateAttendanceMachine(paramMap);
    }

    @Override
    public List<Machine> getMachines() {
        return machineMapper.getMachines();
    }
}
