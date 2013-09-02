/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.model;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.lang3.Range;

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
    public TreeMap<Long,Range> timeCards = new TreeMap<Long,Range>();
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
    
    public static class Range {
        public long endDate;
        public TimeCard timecard;
    }

}