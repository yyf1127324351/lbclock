package com.lb.lbclock.service.impl;

import com.lb.lbclock.dao.OperatorMapper;
import com.lb.lbclock.model.Operator;
import com.lb.lbclock.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperatorServiceImpl implements OperatorService {

    @Autowired
    OperatorMapper operatorMapper;

    @Override
    public List<Operator> getAllOperator() {
        return operatorMapper.getAllOperator();
    }
}
