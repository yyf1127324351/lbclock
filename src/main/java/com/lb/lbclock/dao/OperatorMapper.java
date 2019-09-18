package com.lb.lbclock.dao;

import com.lb.lbclock.model.Operator;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperatorMapper {
    List<Operator> getAllOperator();
}
