/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casa.doughlatorRecipesGenerator;

/**
 *
 * @author rpblanco
 */
public class Logger {
    final public static boolean DEBUG_ENABLE = true;
    final public static boolean DEBUG_DISABLE = false;
    final public static int INFO_MSG = 0;
    final public static int ERROR_MSG = 1;
    final public static int PROCCESS_MSG = 2;
    final private static String INFO_HEADER = "Info: ";
    final private static String ERROR_HEADER = "Error: ";
    final private static String PROCCESS_HEADER = "Proccess: ";
    private OutputMessagesListener listener = null;
    private boolean debugEnable;
    
    public interface OutputMessagesListener
    {
        public void onMessageReceived(String message, int messageType);
    }
      
    public Logger(Object listener)
    {
        debugEnable = false;
        
        try
        {
            this.listener = (OutputMessagesListener)listener;
        }
        catch(ClassCastException e)
        {
            e.printStackTrace();
        }
    }
    
    private String addHeader(String msg, int msgType)
    {
        /* Add header */
        switch(msgType)
        {
            case Logger.ERROR_MSG:
                msg = Logger.ERROR_HEADER + msg;
                break;
            case Logger.INFO_MSG:
                /* Do nothing */
                break;
            case Logger.PROCCESS_MSG:
                msg = Logger.PROCCESS_HEADER + msg;
                break;
            default:
        }

        return msg;
    }
      
    public void log(String msg, int msgType)
    {  
        msg = addHeader(msg, msgType);
        
        if(listener!=null)
            listener.onMessageReceived(msg, msgType);
        
        if(debugEnable==DEBUG_ENABLE)
            System.out.println(msg);
    }
    
    public void log(int value, int msgType)
    {
        String msg = null;
        
        msg = addHeader(String.valueOf(value), msgType);
        
        if(listener!=null)
            listener.onMessageReceived(msg, msgType);

        if(debugEnable==DEBUG_ENABLE)
            System.out.println(msg);   
    }
        
    public void log(Object o, int msgType)
    {
        String msg = null;
        
        msg = addHeader(String.valueOf(o), msgType);
        
        if(listener!=null)
            listener.onMessageReceived(msg, msgType);

        if(debugEnable==DEBUG_ENABLE)
            System.out.println(msg);    
    }
    
    public boolean isDebugEnable() {
        return debugEnable;
    }
    
    public void setDebugEnable(boolean debugEnable) {
        this.debugEnable = debugEnable;
    }
}
