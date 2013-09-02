/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.controller;

import com.github.model.Employee;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public boolean login(String username, String password){
        Employee employee = verfiy(employees.get(username),password);
        return employee.admin;
    }

    private Employee verfiy(Employee employee, String password) {
        if (!employee.password.equals(password))
            throw new RuntimeException("Login Failed Verify Username/password");
        return employee;
        
    }
    
    public void importData(String fileName) 
    {
        File file = null;
        try {
            file = new File(fileName);
            //String output = new Scanner(file).useDelimiter("\\Z").next();
            employees = mapper.readValue(file, new TypeReference<Map<String, Employee>>() {});
        } catch (JsonParseException ex) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException(ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException(ex);
        }catch (IOException ex) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException(ex);
        } 
        insertAdminUser(fileName);
    }
    
    public void insertAdminUser(String fileName)
    {
        if (employees.isEmpty())
        {
            Employee admin = new Employee();
            admin.password = "password";
            admin.admin = true;
            employees.put("admin", admin);
            exportData(fileName);
        }
    }
    public void exportData(String fileName) 
    {
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
        }catch (IOException ex) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } 
    }
}
