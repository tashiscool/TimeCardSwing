/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.controller;

import com.github.model.DateUtils;
import com.github.model.Employee;
import com.github.model.TimeCard;
import com.github.model.WorkDay;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 *
 * @author John
 */
public class EmployeeController {

    Map<String, Employee> employees = new HashMap<String, Employee>();
    ObjectMapper mapper = new ObjectMapper();

    public boolean login(String username, String password) {
        Employee employee = verfiy(employees.get(username), password);
        return employee.admin;
    }

    private Employee verfiy(Employee employee, String password) {
        if (!employee.password.equals(password)) {
            throw new RuntimeException("Login Failed Verify Username/password");
        }
        return employee;

    }

    public void importData(String fileName) {
        File file = null;
        try {
            file = new File(fileName);
            //String output = new Scanner(file).useDelimiter("\\Z").next();
            employees = mapper.readValue(file, new TypeReference<Map<String, Employee>>() {
            });
        } catch (JsonParseException ex) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException(ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException(ex);
        } catch (IOException ex) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException(ex);
        }
        insertAdminUser(fileName);
    }

    public void insertAdminUser(String fileName) {
        if (employees.isEmpty()) {
            Employee admin = new Employee();
            admin.password = "password";
            admin.admin = true;
            employees.put("admin", admin);
            exportData(fileName);
        }
    }

    public void exportData(String fileName) {
        try {
            FileWriter writer = new FileWriter(new File(fileName));
//                Writer strWriter = new StringWriter();
//                mapper.writeValue(strWriter, employees);
//                String userDataJSON = strWriter.toString();
//                String hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(userDataJSON)
//                FileWriter writer = new FileWriter(new File(fileName)); 
//                writer.write(hash); 
            writer.flush();
            mapper.writeValue(writer, employees);
            writer.close();
        } catch (JsonParseException ex) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    static void EmployeePunchin(Employee employee) {
        Long key = Calendar.getInstance().getTimeInMillis();
        Map.Entry<Long, Employee.Range> entry = employee.timeCards.floorEntry(key);
        TimeCard currentTimeCard = null;
//        if (entry == null) {
//            // too small
//            //prolly new employee so create a timecard for this range
//            
//            
//        } else if (key <= entry.getValue().endDate) {
//           currentTimeCard  = entry.getValue().timecard;
//        } else {
//            // too large or in a hole
//        }

        //get a 2 week Timecard OR create a two week time card
        
        //TODO: use vistor pattern
        if (key <= entry.getValue().endDate) {
           currentTimeCard  = entry.getValue().timecard;
        }
        else
        {
            Pair<Long, Long> range = DateUtils.getCurrentTwoWeekPeriod();
            currentTimeCard = new TimeCard();
            currentTimeCard.startDay = new Date(range.getLeft());
            currentTimeCard.endDay = new Date(range.getRight());
            
            Employee.Range currentRange = new Employee.Range();
            currentRange.endDate = currentTimeCard.endDay.getTime();
            employee.timeCards.put(currentTimeCard.startDay.getTime(), currentRange);
        }
        //enter WorkDay Time
        WorkDay currentWorkDay = currentTimeCard.workedDays
                .get(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        
        if (currentWorkDay == null)
        {
            currentWorkDay = new WorkDay();
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int date = Calendar.getInstance().get(Calendar.DATE);
            Calendar c = Calendar.getInstance();
            c.set(year, month, date);
            currentWorkDay.dateWorked = c.getTime();
            int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            currentTimeCard.workedDays.put(dayOfYear, currentWorkDay);
            
        }
        MutablePair<Date, Date> lastpair = null;
        for (MutablePair<Date, Date> time : currentWorkDay.timeInOut) {
            if (time.left != null && time.right == null) {
                throw new RuntimeException("Unable to Time In without timing out for today");
            }
            lastpair = time;
        }
        if (lastpair.right != null)
            currentWorkDay.timeInOut.add(new MutablePair<Date, Date>(Calendar.getInstance().getTime(),null));
    }
    static void EmployeePunchOut(Employee employee) {
        Long key = Calendar.getInstance().getTimeInMillis();
        Map.Entry<Long, Employee.Range> entry = employee.timeCards.floorEntry(key);
        TimeCard currentTimeCard = null;
//        if (entry == null) {
//            // too small
//            //prolly new employee so create a timecard for this range
//            
//            
//        } else if (key <= entry.getValue().endDate) {
//           currentTimeCard  = entry.getValue().timecard;
//        } else {
//            // too large or in a hole
//        }

        //get a 2 week Timecard OR create a two week time card
        
        //TODO: use vistor pattern
        if (key <= entry.getValue().endDate) {
           currentTimeCard  = entry.getValue().timecard;
        }
        else
        {
//            Pair<Long, Long> range = DateUtils.getCurrentTwoWeekPeriod();
//            currentTimeCard = new TimeCard();
//            currentTimeCard.startDay = new Date(range.getLeft());
//            currentTimeCard.endDay = new Date(range.getRight());
//            
//            Employee.Range currentRange = new Employee.Range();
//            currentRange.endDate = currentTimeCard.endDay.getTime();
//            employee.timeCards.put(currentTimeCard.startDay.getTime(), currentRange);
            throw new RuntimeException("Can't Punch out you. never Punched in");
        }
        //enter WorkDay Time
        WorkDay currentWorkDay = currentTimeCard.workedDays
                .get(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        
        if (currentWorkDay == null)
        {
            throw new RuntimeException("Can't Punch out you. never Punched in Today!");            
        }
        MutablePair<Date, Date> lastpair = null;
        for (MutablePair<Date, Date> time : currentWorkDay.timeInOut) {
            lastpair = time;
        }
        lastpair.right = Calendar.getInstance().getTime() ;
        
        
        
    }  
}
