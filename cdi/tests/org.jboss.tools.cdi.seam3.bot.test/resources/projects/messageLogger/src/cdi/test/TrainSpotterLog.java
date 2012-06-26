package cdi.test;

import org.jboss.solder.logging.MessageLogger;
import org.jboss.solder.logging.internal.LogMessage;
import org.jboss.solder.messages.Message;

@MessageLogger
public interface TrainSpotterLog {
   @LogMessage @Message("Spotted %s diesel trains") 
   void dieselTrainsSpotted(int number);
}
