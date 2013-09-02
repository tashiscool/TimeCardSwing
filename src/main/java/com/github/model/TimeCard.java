/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author John
 */
public class TimeCard {
    public List<WorkDay> workedDays = new ArrayList<WorkDay>();
    public Date startDay;
    public Date endDay;
}
