package com.collective.celos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class TestingAppender extends AppenderSkeleton {

  //We are collecting all the log messages in to a list
  private List<String> messages = new ArrayList<String>();

  // This method is called when ever any logging happens
  // We are simply taking the log message and adding to our list
  @Override
  protected void append(LoggingEvent event) {
    messages.add(event.getRenderedMessage());
  }

  //This method is called when the appender is closed.
  //Gives an opportunity to clean up resources
  public void close() {
    messages.clear();
  }

  @Override
  public boolean requiresLayout() {
    return false;
  }

  public String[] getMessages() {
    return (String[]) messages.toArray(new String[messages.size()]);
  }

  public void clear() {
    messages.clear();
  }
}