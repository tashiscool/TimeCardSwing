/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author John
 */
public class TimeCard {
    public Map<Integer,WorkDay> workedDays = new LinkedHashMap<Integer,WorkDay>();
    public Date startDay;
    public Date endDay;
    
}
