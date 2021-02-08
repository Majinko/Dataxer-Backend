package com.data.dataxer.services;

import com.data.dataxer.repositories.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class SalaryServiceImpl implements SalaryService {
    @Autowired
    private SalaryRepository salaryRepository;
}
