/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.model;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.tuple.MutablePair;

/**
 *
 * @author John
 */
public class WorkDay {
   public int dayOfWeek = 0;
   public List<MutablePair<Date,Date>> timeInOut = new LinkedList<MutablePair<Date, Date>>();
            
}
