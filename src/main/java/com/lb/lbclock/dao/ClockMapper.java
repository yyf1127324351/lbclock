package com.lb.lbclock.dao;

import com.lb.lbclock.model.Clock;
import org.springframework.stereotype.Repository;

@Repository
public interface ClockMapper {
    void addClockRecord(Clock clock);

    void updateClockRecord(Clock clock);
}
