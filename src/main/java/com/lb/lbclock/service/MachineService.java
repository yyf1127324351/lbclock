package com.lb.lbclock.service;

import com.lb.lbclock.model.Machine;

import java.util.List;
import java.util.Map;

public interface MachineService {
    void updateAttendanceMachine(Map<String, Object> paramMap);

    List<Machine> getMachines();
}
