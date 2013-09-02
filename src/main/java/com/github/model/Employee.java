/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author John
 */
public class Employee {
    enum EmpStatus
    {
        PAYROLLED,
        NONPAYROLLED
    }
    public List<TimeCard> timeCards = new LinkedList<TimeCard>();
    public String firstName;
    public String lastName;
    public String username;
    public String password;
    
    public double hourlyWage;
    public double accruedTimeOff;
    
    public boolean paidHolidays;
    
    public EmpStatus status;
    
    public Date Birthday;
    public Date Hiredate;
    
    public boolean admin;
    
}
