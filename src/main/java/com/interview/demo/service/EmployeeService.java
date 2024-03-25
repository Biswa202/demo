package com.interview.demo.service;

import com.interview.demo.pojo.Employee;
import com.interview.demo.repo.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public void addEmployee(Employee employee) {
        // Additional validation logic if needed
        employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Map<String, Object>> calculateTaxDeduction() {

        List<Map<String, Object>> deductions = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        getAllEmployees().forEach(employee -> {
            LocalDate doj = employee.getDoj();
            LocalDate startOfYear = LocalDate.of(currentYear, Month.APRIL, 1);
            LocalDate startOfMonth = doj.isAfter(startOfYear) ? doj : startOfYear;
            LocalDate endOfMonth = today.isBefore(LocalDate.of(currentYear + 1, Month.APRIL, 1)) ? today : LocalDate.of(currentYear + 1, Month.MARCH, 31);
            double totalSalary = calculateTotalSalary(employee, startOfMonth, endOfMonth);

            if (totalSalary > 0) {
                double taxAmount = calculateTax(totalSalary);
                double cessAmount = totalSalary > 2500000 ? (totalSalary - 2500000) * 0.02 : 0;
                Map<String, Object> deduction = new HashMap<>();
                deduction.put("Employee Code", employee.getEmployeeId());
                deduction.put("First Name", employee.getFirstName());
                deduction.put("Last Name", employee.getLastName());
                deduction.put("Yearly Salary", totalSalary);
                deduction.put("Tax Amount", taxAmount);
                deduction.put("Cess Amount", cessAmount);
                deductions.add(deduction);
            }
        });



        return deductions;
    }

    private void validateEmployeeData(Employee employee) {
        // Add validation logic here
    }

    private double calculateTotalSalary(Employee employee, LocalDate startOfMonth, LocalDate endOfMonth) {
        double totalSalary = employee.getSalary() * ((endOfMonth.getYear() - startOfMonth.getYear()) * 12 + endOfMonth.getMonthValue() - startOfMonth.getMonthValue() + 1);
        return totalSalary;
    }

    private double calculateTax(double totalSalary) {
        if (totalSalary <= 250000)
            return 0;
        else if (totalSalary <= 500000)
            return (totalSalary - 250000) * 0.05;
        else if (totalSalary <= 1000000)
            return 12500 + (totalSalary - 500000) * 0.1;
        else
            return 62500 + (totalSalary - 1000000) * 0.2;
    }
}
