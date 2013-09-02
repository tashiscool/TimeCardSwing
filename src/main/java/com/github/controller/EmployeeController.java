/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.controller;

import com.github.model.DateUtils;
import com.github.model.Employee;
import com.github.model.TimeCard;
import com.github.model.WorkDay;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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

import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.commons.io.IOUtils;
import org.opensaml.ws.message.decoder.MessageDecodingException;


import org.opensaml.xml.util.Base64;

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
        //get a 2 week Timecard OR create a two week time card

        //TODO: use vistor pattern
        if (key <= entry.getValue().endDate) {
            currentTimeCard = entry.getValue().timecard;
        } else {
            currentTimeCard = generateTimeCard(employee);
        }
        //enter WorkDay Time
        WorkDay currentWorkDay = currentTimeCard.workedDays
                .get(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));

        if (currentWorkDay == null) {
            currentWorkDay = generateWorkDay(currentTimeCard, employee);
        }
        MutablePair<Date, Date> lastpair = null;
        for (MutablePair<Date, Date> time : currentWorkDay.timeInOut) {
            if (time.left != null && time.right == null) {
                throw new RuntimeException("Unable to Time In without timing out for today");
            }
            lastpair = time;
        }
        if (lastpair != null && lastpair.right != null) {
            currentWorkDay.timeInOut.add(new MutablePair<Date, Date>(Calendar.getInstance().getTime(), null));
        } else {
            throw new RuntimeException("Can't Punch out you. never Punched in");
        }
    }

    static void EmployeePunchOut(Employee employee) {
        Long key = Calendar.getInstance().getTimeInMillis();
        Map.Entry<Long, Employee.Range> entry = employee.timeCards.floorEntry(key);
        TimeCard currentTimeCard = null;

        //TODO: use vistor pattern
        if (key <= entry.getValue().endDate) {
            currentTimeCard = entry.getValue().timecard;
        } else {
            throw new RuntimeException("Can't Punch out you. never Punched in");
        }
        //enter WorkDay Time
        WorkDay currentWorkDay = currentTimeCard.workedDays
                .get(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));

        if (currentWorkDay == null) {
            throw new RuntimeException("Can't Punch out you. never Punched in Today!");
        }
        MutablePair<Date, Date> lastpair = null;
        for (MutablePair<Date, Date> time : currentWorkDay.timeInOut) {
            lastpair = time;
        }
        if (lastpair != null) {
            lastpair.right = Calendar.getInstance().getTime();
        } else {
            throw new RuntimeException("Can't Punch out you. never Punched in");
        }
    }

    private static WorkDay generateWorkDay(TimeCard currentTimeCard, Employee employee) {
        WorkDay currentWorkDay = new WorkDay();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int date = Calendar.getInstance().get(Calendar.DATE);
        Calendar c = Calendar.getInstance();
        c.set(year, month, date);
        currentWorkDay.dateWorked = c.getTime();
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        currentTimeCard.workedDays.put(dayOfYear, currentWorkDay);
        return currentWorkDay;
    }

    private static TimeCard generateTimeCard(Employee employee) {
        Pair<Long, Long> range = DateUtils.getCurrentTwoWeekPeriod();
        TimeCard currentTimeCard = new TimeCard();
        currentTimeCard.startDay = new Date(range.getLeft());
        currentTimeCard.endDay = new Date(range.getRight());

        Employee.Range currentRange = new Employee.Range();
        currentRange.endDate = currentTimeCard.endDay.getTime();
        employee.timeCards.put(currentTimeCard.startDay.getTime(), currentRange);
        return currentTimeCard;
    }

    public static String decodeURL(String message)
            throws MessageDecodingException, UnsupportedEncodingException {
//		message = URLDecoder.decode(message, "UTF-8");
        byte[] decodedBytes = Base64.decode(message);
        if (decodedBytes == null) {
            throw new MessageDecodingException(
                    "Unable to Base64 decode incoming message");
        }

        try {
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(
                    decodedBytes);
            String string = new String(decodedBytes);
            InflaterInputStream inflater = new InflaterInputStream(bytesIn,
                    new Inflater(true));
            StringWriter writer = new StringWriter();
            IOUtils.copy(inflater, writer, "UTF-8");
            String theString = writer.toString();
            return theString;
        } catch (Exception e) {
            throw new MessageDecodingException(
                    "Unable to Base64 decode and inflate SAML message", e);
        }
    }

    public static String encodeURL(String messageXML) throws IOException {
        Deflater deflater = new Deflater(Deflater.DEFLATED, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(
                byteArrayOutputStream, deflater);
        deflaterOutputStream.write(messageXML.getBytes());
        deflaterOutputStream.close();
        String samlResponse = Base64.encodeBytes(byteArrayOutputStream.toByteArray(),
                Base64.DONT_BREAK_LINES);//Encode downstream
        return samlResponse;
    }
}
