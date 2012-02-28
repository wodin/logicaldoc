package com.logicaldoc.benchmark;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.icu.text.SimpleDateFormat;

public class Util {

	protected static Log log = LogFactory.getLog(Util.class);

	public static String formatTimeSpan(long diff){
		Date d=new Date(); 
		d=DateUtils.setDays(d, 0);
		d=DateUtils.setHours(d, 0);
		d=DateUtils.setMinutes(d, 0);
		d=DateUtils.setSeconds(d, 0);
		d=DateUtils.setMilliseconds(d, 0);
		d=DateUtils.addMilliseconds(d, (int)diff);

		SimpleDateFormat df=new SimpleDateFormat("HH:mm:ss.SS");
        return df.format(d);	
	}
	
	public static String formatFriendlyTimeSpan(long diff) {
		StringBuffer sb = new StringBuffer();
		long diffInSeconds = diff / 1000;

		long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
		long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
		long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
		long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
		long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
		long years = (diffInSeconds = (diffInSeconds / 12));

		if (years > 0) {
			if (years == 1) {
				sb.append(" year");
			} else {
				sb.append(years + " years");
			}
			if (years <= 6 && months > 0) {
				if (months == 1) {
					sb.append(" and a month");
				} else {
					sb.append(" and " + months + " months");
				}
			}
		} else if (months > 0) {
			if (months == 1) {
				sb.append("a month");
			} else {
				sb.append(months + " months");
			}
			if (months <= 6 && days > 0) {
				if (days == 1) {
					sb.append(" and 1d");
				} else {
					sb.append(" and " + days + "d");
				}
			}
		} else if (days > 0) {
			if (days == 1) {
				sb.append("1d");
			} else {
				sb.append(days + "d");
			}
			if (days <= 3 && hrs > 0) {
				if (hrs == 1) {
					sb.append(", 1h");
				} else {
					sb.append(", " + hrs + "h");
				}
			}
		} else if (hrs > 0) {
			if (hrs == 1) {
				sb.append("1h");
			} else {
				sb.append(hrs + "h");
			}
			if (min > 1) {
				sb.append(", " + min + "m");
			}
		} else if (min > 0) {
			if (min == 1) {
				sb.append("1m");
			} else {
				sb.append(min + "m");
			}
			if (sec > 1) {
				sb.append(" and " + sec + "s");
			}
		} else {
			if (sec <= 1) {
				sb.append(diff + "ms");
			} else {
				sb.append(sec + "s");
			}
		}

		return sb.toString();
	}

}
