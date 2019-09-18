package com.lb.lbclock.dao;

import com.lb.lbclock.model.Machine;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MachineMapper {
    void updateAttendanceMachine(Map<String, Object> paramMap);

    List<Machine> getMachines();
}
