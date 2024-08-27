package com.example.lotterysystem.common.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

import java.util.logging.Filter;

public class InfoLevelFilter extends Filter<ILoggingEvent> {

    public FilterReply decide(ILoggingEvent iLoggingEvent){
        if(iLoggingEvent.getLevel().toInt() == Level.INFO.toInt()){
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }

}
